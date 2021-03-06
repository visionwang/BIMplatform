package cn.dlb.bim.ifc.stream.query.multithread;

/******************************************************************************
 * Copyright (C) 2009-2016  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see {@literal<http://www.gnu.org/licenses/>}.
 *****************************************************************************/

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import cn.dlb.bim.database.DatabaseException;
import cn.dlb.bim.ifc.stream.VirtualObject;
import cn.dlb.bim.ifc.stream.WrappedVirtualObject;
import cn.dlb.bim.ifc.stream.query.CanInclude;
import cn.dlb.bim.ifc.stream.query.Include;
import cn.dlb.bim.ifc.stream.query.QueryContext;
import cn.dlb.bim.ifc.stream.query.QueryException;
import cn.dlb.bim.ifc.stream.query.QueryPart;

public class RunnableQueryIncludeStackFrame extends RunnableDatabaseReadingStackFrame {

	private Set<Short> outputFilterCids;
	private Iterator<EReference> featureIterator;
	private CanInclude previousInclude;
	private Include include;
	private EReference feature;

	public RunnableQueryIncludeStackFrame(MultiThreadQueryObjectProvider queryObjectProvider, QueryContext queryContext, CanInclude previousInclude, Include include, VirtualObject currentObject, QueryPart queryPart) throws QueryException, DatabaseException {
		super(queryContext, queryObjectProvider, queryPart);
		this.previousInclude = previousInclude;
		this.include = include;
		this.currentObject = currentObject;
		
		List<EReference> features = include.getFields();
		
		if (features == null || features.isEmpty()) {
			setStatus(Status.DONE);
			return;
		}
		featureIterator = features.iterator();
		if (include.getOutputTypes() != null) {
			this.outputFilterCids = new HashSet<>();
			for (EClass eClass : include.getOutputTypes()) {
				short cid = queryObjectProvider.getCatalogService().getCidOfEClass(eClass);
				outputFilterCids.add(cid);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean process() throws DatabaseException, QueryException, InterruptedException {
		
		while (featureIterator.hasNext()) {
			feature = featureIterator.next();
			
			Object value = currentObject.eGet(feature);
			
			if (value != null) {
				if (feature.isMany()) {
					List<Long> list = (List<Long>)value;
					for (Object r : list) {
						if (r instanceof Long) {
							processReference((Long)r);
						} else {
							// ??
						}
					}
				} else {
					if (value instanceof Long) {
						long refOid = (Long) value;
						processReference(refOid);
					} else if (value instanceof WrappedVirtualObject) {
						// ??
					}
				}
			}
		}
		getQueryObjectProvider().addToStorage(currentObject);
		return true;
	}

	private void processReference(long refOid) {
		if (outputFilterCids == null || outputFilterCids.contains((short)refOid)) {
			getQueryObjectProvider().push(new RunnableFollowReferenceStackFrame(getQueryObjectProvider(), refOid, getReusable(), getQueryPart(), feature, include));
		}
	}
	
	@Override
	public String toString() {
		return "RunnableQueryIncludeStackFrame (eClass: " + currentObject.eClass().getName() + ", oid: " + currentObject.getOid() + ")" +
				" QueryPart: " + (getQueryPart() == null ? "null" : getQueryPart().hashCode()) + " PreviousInclude : " + (getPreviousInclude() == null ? "null" : getPreviousInclude().hashCode()) + " Include: " + (getInclude() == null ? "null" : String.valueOf(getInclude().hashCode()));
	}
	
	public CanInclude getPreviousInclude() {
		return previousInclude;
	}

	public Include getInclude() {
		return include;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(getClass()).append(getQueryObjectProvider()).append(getReusable()).append(getQueryPart()).append(previousInclude)
		.append(include).append(currentObject.getOid());
		return hashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RunnableQueryIncludeStackFrame) {
			RunnableQueryIncludeStackFrame stackFrame = (RunnableQueryIncludeStackFrame) o;
			if (stackFrame.getQueryObjectProvider() == getQueryObjectProvider() && stackFrame.getReusable() == getReusable() 
					&& stackFrame.getQueryPart() == getQueryPart() && stackFrame.getPreviousInclude() == getPreviousInclude() 
					&& stackFrame.getInclude() == getInclude() && currentObject.getOid() == getCurrentObject().getOid()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}
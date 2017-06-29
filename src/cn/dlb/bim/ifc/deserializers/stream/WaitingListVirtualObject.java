package cn.dlb.bim.ifc.deserializers.stream;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.EClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dlb.bim.ifc.database.DatabaseException;
import cn.dlb.bim.ifc.deserializers.DeserializeException;
import cn.dlb.bim.service.PlatformService;

public class WaitingListVirtualObject<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(WaitingListVirtualObject.class);
	private final Map<T, List<WaitingVirtualObject>> waitingObjects = new HashMap<T, List<WaitingVirtualObject>>();
	private final Map<Long, AtomicInteger> openConnections = new HashMap<>();
	private PlatformService platformService;
	
	public WaitingListVirtualObject(PlatformService platformService) {
		this.platformService = platformService;
	}

	public boolean containsKey(T recordNumber) {
		return waitingObjects.containsKey(recordNumber);
	}

	private AtomicInteger getOpenConnectionCounter(long oid) {
		AtomicInteger atomicInteger = openConnections.get(oid);
		if (atomicInteger == null) {
			atomicInteger = new AtomicInteger();
			openConnections.put(oid, atomicInteger);
		}
		return atomicInteger;
	}
	
	public void add(T referenceId, WaitingVirtualObject waitingObject) {
		getOpenConnectionCounter(waitingObject.getObject().getOid()).incrementAndGet();
		
		List<WaitingVirtualObject> waitingList = null;
		if (waitingObjects.containsKey(referenceId)) {
			waitingList = waitingObjects.get(referenceId);
		} else {
			waitingList = new ArrayList<WaitingVirtualObject>();
			waitingObjects.put(referenceId, waitingList);
		}
		waitingList.add(waitingObject);
	}
	
	private void decrementOpenConnections(VirtualObject virtualObject) throws DatabaseException {
		AtomicInteger openConnectionCounter = getOpenConnectionCounter(virtualObject.getOid());
		int decrementAndGet = openConnectionCounter.decrementAndGet();
		if (decrementAndGet == 0) {
			platformService.save(virtualObject);
		} else if (decrementAndGet < 0) {
			throw new DatabaseException("Inconsistent state");
		}
	}
	
	public void updateNode(T expressId, EClass ec, VirtualObject eObject) throws DeserializeException, DatabaseException {
		for (WaitingVirtualObject waitingObject : waitingObjects.get(expressId)) {
			EClass objectEClass = platformService.getEClassForCid(eObject.getEClassId());
			if (waitingObject.getStructuralFeature().isMany()) {
				ListWaitingVirtualObject listWaitingObject = (ListWaitingVirtualObject)waitingObject;
				if (((EClass) waitingObject.getStructuralFeature().getEType()).isSuperTypeOf(objectEClass)) {
					waitingObject.getObject().setListItemReference(waitingObject.getStructuralFeature(), listWaitingObject.getIndex(), eObject.getOid());
					decrementOpenConnections(waitingObject.getObject());
				} else {
					throw new DeserializeException(waitingObject.getLineNumber(), "Field " + waitingObject.getStructuralFeature().getName() + " of "
							+ waitingObject.getStructuralFeature().getEContainingClass().getName() + " cannot contain a " + objectEClass.getName());
				}
			} else {
				if (((EClass) waitingObject.getStructuralFeature().getEType()).isSuperTypeOf(objectEClass)) {
					waitingObject.getObject().setReference(waitingObject.getStructuralFeature(), eObject.getOid());
					decrementOpenConnections(waitingObject.getObject());
				} else {
					throw new DeserializeException(waitingObject.getLineNumber(), "Field " + waitingObject.getStructuralFeature().getName() + " of "
							+ waitingObject.getStructuralFeature().getEContainingClass().getName() + " cannot contain a " + objectEClass.getName() + "/" + eObject.getOid());
				}
			}
		}
		waitingObjects.remove(expressId);
	}

	public int size() {
		return waitingObjects.size();
	}
	
	public void dumpIfNotEmpty() throws WaitingListException {
		if (size() > 0) {
			for (Entry<T, List<WaitingVirtualObject>> entry : waitingObjects.entrySet()) {
				StringBuilder sb = new StringBuilder("" + entry.getKey() + " ");
				for (WaitingVirtualObject waitingObject : entry.getValue()) {
					sb.append(waitingObject.toString() + " ");
				}
				LOGGER.info(sb.toString());
			}
			throw new WaitingListException("Waitinglist not empty, this usually means some objects were referred, but not included in the download");
		}
	}
}
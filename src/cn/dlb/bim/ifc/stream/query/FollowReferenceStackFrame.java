package cn.dlb.bim.ifc.stream.query;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import cn.dlb.bim.database.DatabaseException;

public class FollowReferenceStackFrame extends DatabaseReadingStackFrame implements ObjectProvidingStackFrame {

	private long oid;
	private boolean hasRun = false;
	private Include include;
	private EReference fromReference;
	
	public FollowReferenceStackFrame(QueryObjectProvider queryObjectProvider, Long oid, QueryContext reusable, QueryPart queryPart, EReference fromReference, Include include) {
		super(reusable, queryObjectProvider, queryPart);
		this.oid = oid;
		this.fromReference = fromReference;
		this.include = include;
	}

	@Override
	public boolean process() throws DatabaseException, QueryException {
		if (getQueryObjectProvider().hasRead(oid)) {
			processPossibleIncludes(null, include);
			return true;
		}
		
		if (hasRun) {
			return true;
		}
		hasRun = true;
		if (oid == -1) {
			throw new DatabaseException("Cannot get object for oid " + oid);
		}
		Integer rid = getReusable().getRid();
		currentObject = getQueryObjectProvider().getVirtualObjectService().findOneByRidAndOid(rid, oid);
		decideUseForSerialization(currentObject);
		processPossibleIncludes(null, include);
		return true;
	}
	
	@Override
	public String toString() {
		try {
			EClass eClass = getQueryObjectProvider().getCatalogService().getEClassForOid(oid);
			return "FollowReferenceStackFrame (" + eClass.getName() + "." + fromReference.getName() + ", " + oid + ")";
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return "FollowReferenceStackFrame (" + fromReference.getName() + ")";
	}
}
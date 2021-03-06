package cn.dlb.bim.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;
import cn.dlb.bim.dao.VirtualObjectDao;
import cn.dlb.bim.ifc.stream.VirtualObject;

@Repository("VirtualObjectDaoImpl")
public class VirtualObjectDaoImpl extends AbstractBaseMongoDao<VirtualObject> implements VirtualObjectDao {

	@Autowired
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
//	@Cacheable(value = "virtualObject")
	public VirtualObject findOneByRidAndOid(Integer rid, Long oid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("oid").is(oid).andOperator(Criteria.where("rid").is(rid)));
		return findOne(query);
	}
	
	public Collection<VirtualObject> findByRidAndOids(Integer rid, Collection<Long> oids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("oid").in(oids).andOperator(Criteria.where("rid").is(rid)));
		return find(query);
	}

	@Override
	public VirtualObject findOneByRidAndCid(Integer rid, Short cid) {

		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(rid).andOperator(Criteria.where("eClassId").is(cid)));

		return findOne(query);
	}

	@Override
	public Collection<VirtualObject> findByRidAndCid(Integer rid, Short cid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(rid).andOperator(Criteria.where("eClassId").is(cid)));
		return find(query);
	}

	@Override
	public CloseableIterator<VirtualObject> streamByRidAndCid(Integer rid, Short cid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(rid).andOperator(Criteria.where("eClassId").is(cid)));
		return stream(query);
	}

	@Override
	public VirtualObject update(VirtualObject virtualObject) {
		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(virtualObject.getRid())
				.andOperator(Criteria.where("oid").is(virtualObject.getOid())));
		Update update = new Update();
		update.set("eClassId", virtualObject.getEClassId()).set("features", virtualObject.getFeatures());

		return update(query, update);
	}

	@Override
	public Collection<VirtualObject> findByRidAndCids(Integer rid, Collection<Short> cids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(rid).andOperator(Criteria.where("eClassId").in(cids)));
		return find(query);
	}

	@Override
	public CloseableIterator<VirtualObject> streamByRid(Integer rid) {

		Query query = new Query();
		query.addCriteria(Criteria.where("rid").is(rid));

		return mongoTemplate.stream(query, VirtualObject.class);
	}

	@Override
	public int updateAllVirtualObject(Collection<VirtualObject> virtualObjects) {
		List<BatchUpdateOptions> options = new ArrayList<>();
		for (VirtualObject virtualObject : virtualObjects) {
			Query query = new Query();
			query.addCriteria(Criteria.where("rid").is(virtualObject.getRid())
					.andOperator(Criteria.where("oid").is(virtualObject.getOid())));
			Update update = new Update();
			update.set("eClassId", virtualObject.getEClassId()).set("features", virtualObject.getFeatures());
			options.add(new BatchUpdateOptions(query, update, true, true));

		}
		return updateAll(options);
	}

}

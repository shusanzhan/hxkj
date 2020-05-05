package com.ystech.core.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.core.util.PropertyFilter;
import com.ystech.core.util.PropertyFilter.MatchType;
import com.ystech.core.util.ReflectionUtils;


/**
 * 封装SpringSide扩展功能的Hibernat DAO泛型基类.
 * 
 * 扩展功能包括分页查询,按属性过滤条件列表查询. 可在Service层直接使用,也可以扩展泛型DAO子类使用,见两个构造函数的注释.
 * 
 * @param <T>
 *            DAO操作的对象类型
 * 
 * @author shusanzhan
 */
public class HibernateEntityDao<T> extends SimpleHibernateDao<T>{
	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
	 * HibernateDao<User, Long>{ }
	 */
	public HibernateEntityDao() {
		super();
	}

	/**
	 * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数. 在构造函数中定义对象类型Class. eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User,
	 * Long>(sessionFactory, User.class);
	 */
	public HibernateEntityDao(SessionFactory sessionFactory ,Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}
	
	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType
	 *            匹配方式,目前支持的取值见PropertyFilter的MatcheType enum.
	 */
	public List findBy(String propertyName, Object value,MatchType matchType) {
		Criterion criterion = buildPropertyFilterCriterion(propertyName, value,matchType);
		return find(criterion);
	}

	/**
	 * 按属性过滤条件列表查找对象列表.
	 */
	public List find(List<PropertyFilter> filters) {
		Criterion[] criterions = buildPropertyFilterCriterions(filters);
		return find(criterions);
	}

	/**
	 * 分页获取全部对象.
	 */
	public Page<T> getAll(int curentPage, int pageSize) {
		return pageQuery(curentPage, pageSize);
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数.不支持其中的orderBy参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	public Page pageQuery(int currentPage, int pageSize, String hql,Object... values) {
		long totalCount =countHqlResult(hql, values);
		if (totalCount < 1) {
			return new Page();
		}
		Query query = createQuery(hql, values);
		int start = Page.getStartOfPage(currentPage, pageSize);
		List<T> result = query.setFirstResult(start).setMaxResults(pageSize).list();
		return new Page(start, totalCount, pageSize, result);
	}
	
	/**
	 * 按Criteria分页查询.
	 * 
	 * @param page
	 *            分页参数.
	 * @param criterions
	 *            数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings("unchecked")
	public Page pageQuery(int pageNo, int pageSize,Criterion... criterions) {
		Criteria criteria = createCriteria(criterions);
		int totalCount = countCriteriaResult(criteria);
		if (totalCount < 1) {
			return new Page();
		}
		int start = Page.getStartOfPage(pageNo, pageSize);
		List result = criteria.setFirstResult(start).setMaxResults(pageSize).list();
		return new Page(start, totalCount, pageSize, result);
	}


	/**
	 * 按属性过滤条件列表分页查找对象.
	 */
	public Page pageQuery(int curentPage, int pageSize,
			List<PropertyFilter> filters) {
		Criterion[] criterions = buildPropertyFilterCriterions(filters);
		return pageQuery(curentPage, pageSize, criterions);
	}


	/**
	 * 通过原生sql查询分页;如果需要排序，直接在sql后面写；
	 * sql语句可以是：select * from user where dbid=1；也可以是：select * from user where dbid =? ,然后在用values进行传参;
	 * @param pageNo
	 * @param pageSize
	 * @param sql
	 * @param entities
	 * @return
	 */
	public Page pagedQuerySql(int pageNo,int pageSize,Class<T> entitsClass,String sql,Object...values ){
		System.out.println("======"+sql);
		long count = countSqlResult(sql, values);
		if(count<1){
			new Page<T>();
		}
		int start = Page.getStartOfPage(pageNo, pageSize);
		List<T> list = executeSqlQuery(entitsClass,sql, values).setFirstResult(start).setMaxResults(pageSize).list();
		return new Page(start, count, pageSize, list);
	}
	 
	public List<Object[]> createSQLQurey(String sql){
		List<Object[]> list = executeSqlQuery(sql, null).list();
		return list;
	}
	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	public boolean isPropertyUnique(String propertyName,Object newValue, Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(propertyName, newValue);
		return (object == null);
	}
	/**
	 * 通过原生sql进行数据统计,原生sql的变量，必须与hql对应；
	 * 通过hql进行数据查询分页
	 * @param pageNo
	 * @param pageSize
	 * @param hql
	 * @param countSql
	 * @param values
	 * @return
	 */
	public Page pagedQueryHqlSql(int pageNo,int pageSize,String countSql,String hql,Object...values ){
		System.out.println("======"+countSql);
		BigInteger countBigInt = count(countSql, values);
		long count = countBigInt.longValue();
		if(count<1){
			return new Page<T>();
		}
		int start = Page.getStartOfPage(pageNo, pageSize);
		List<T> list = createQuery(hql, values).setFirstResult(start).setMaxResults(pageSize).list();
		return new Page(start, count, pageSize, list);
	}
	 /**
     * 反射映射数据库表字段到实体层，需要实体层必须包括成员变量和对应的public set方法 如: private int id; 对应public
     * void setId(int id);方法 成员变量不考虑大小写，默认会忽略数据库字段下划线。如：user_id 等于userId
     *
     * @param <T>
     * @param entityClass 需要返回的实体类类型
     * @param sql 参数 sql 查询语句
     * @param arr 可变参数，有则传，没有可忽略
	 * @return
	 */
	public List<T> createQuerySql(String sql, Class<T> entityClass, Object... values){
		List<T> list = new ArrayList<T>();
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			if(null!=values){
				for (int i = 0; i < values.length; i++) {
					createStatement.setObject(i + 1, values[i]);
				}
			}
			ResultSet resultSet = createStatement.executeQuery();
			if(null==resultSet){
				return null;
			}
			list= ResultSetUtil.getDateResult(list, resultSet, entityClass);
			resultSet.close();
			createStatement.close();
			jdbcConnection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public Object countBySql(String sql) {
		Session session = this.getSession();
		Object object = session.createSQLQuery(sql).uniqueResult();
		return object;
	}
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数. 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 * @param hql
	 * @param values
	 * @return
	 */
	public long countSqlResult(String sql, Object... values) {
		String fromHql = sql.toLowerCase();
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from" + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
		String countSql = "select count(*) " + fromHql;
		
		try {
			BigInteger count = count(countSql, values);
			return count.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("hql can't be auto count, hql is:"	+ countSql, e);
		}
	}
	public Double sumSqlResult(String sql,Object...values){
		Session session = this.getSession();
		Query queryObject = session.createSQLQuery(sql);
		if(values!=null){
			for(int i=0;i<values.length;i++){
				queryObject.setParameter(i, values[i]);
			}
		}
		List u1 = queryObject.list();
		if(u1.get(0)!=null){
			String result = u1.get(0).toString();
			return Double.parseDouble(result);
		}else{
			Double result = new Double(0);
			return result;
		}
	}
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数. 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 * @param hql
	 * @param values
	 * @return
	 */
	public long countSql(String sql, Object... values) {
		try {
			BigInteger count = count(sql, values);
			return count.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("hql can't be auto count, hql is:"	+ sql, e);
		}
	}
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数. 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 * @param hql
	 * @param values
	 * @return
	 */
	protected long countHqlResult(String sql, Object... values) {
		String fromHql = sql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from" + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
		String countSql = "select count(*) " + fromHql;
		
		try {
			BigInteger count = count(countSql, values);
			return count.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("hql can't be auto count, hql is:"	+ countSql, e);
		}
	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	@SuppressWarnings("unchecked")
	protected int countCriteriaResult(Criteria criteria) {
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = criteriaImpl.getProjection();
		ResultTransformer transformer = criteriaImpl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(criteriaImpl,
					"orderEntries");
			ReflectionUtils
					.setFieldValue(criteriaImpl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		// 执行Count查询
		int totalCount = (Integer) criteriaImpl.setProjection(Projections.rowCount())
				.uniqueResult();

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		criteriaImpl.setProjection(projection);

		if (projection == null) {
			criteriaImpl.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			criteriaImpl.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(criteriaImpl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		return totalCount;
	}

	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
	 */
	protected Criterion[] buildPropertyFilterCriterions(
			List<PropertyFilter> filters) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		for (PropertyFilter filter : filters) {
			if (!filter.isMultiProperty()) { // 只有一个属性需要比较的情况.
				Criterion criterion = buildPropertyFilterCriterion(filter.getPropertyName(), filter.getPropertyValue(),filter.getMatchType());
				criterionList.add(criterion);
			} else {// 包含多个属性需要比较的情况,进行or处理.
				Disjunction disjunction = Restrictions.disjunction();
				for (String param : filter.getPropertyNames()) {
					Criterion criterion = buildPropertyFilterCriterion(param,filter.getPropertyValue(), filter.getMatchType());
					disjunction.add(criterion);
				}
				criterionList.add(disjunction);
			}
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}

	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildPropertyFilterCriterion(String propertyName,
			Object propertyValue, MatchType matchType) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = null;
		try {

			// 根据MatchType构造criterion
			if (MatchType.EQ.equals(matchType)) {
				criterion = Restrictions.eq(propertyName, propertyValue);
			} else if (MatchType.LIKE.equals(matchType)) {
				criterion = Restrictions.like(propertyName,
						(String) propertyValue, MatchMode.ANYWHERE);
			} else if (MatchType.LE.equals(matchType)) {
				criterion = Restrictions.le(propertyName, propertyValue);
			} else if (MatchType.LT.equals(matchType)) {
				criterion = Restrictions.lt(propertyName, propertyValue);
			} else if (MatchType.GE.equals(matchType)) {
				criterion = Restrictions.ge(propertyName, propertyValue);
			} else if (MatchType.GT.equals(matchType)) {
				criterion = Restrictions.gt(propertyName, propertyValue);
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}
		return criterion;
	}

	/**
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql   + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
}

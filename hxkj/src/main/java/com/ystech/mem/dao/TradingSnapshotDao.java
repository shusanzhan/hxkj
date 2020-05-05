/**
 * 
 */
package com.ystech.mem.dao;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.mem.model.TradingSnapshot;

/**
 * @author shusanzhan
 * @date 2014-9-12
 */
@Component("tradingSnapshotManageImpl")
public class TradingSnapshotDao extends HibernateEntityDao<TradingSnapshot>{

}

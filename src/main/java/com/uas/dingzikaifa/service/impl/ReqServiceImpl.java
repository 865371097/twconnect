package com.uas.dingzikaifa.service.impl;

import com.uas.dingzikaifa.dao.BaseDao;
import com.uas.dingzikaifa.service.ReqService;
import com.uas.dingzikaifa.util.BaseUtil;
import com.uas.dingzikaifa.util.JsonUtil;
import com.uas.dingzikaifa.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReqServiceImpl implements ReqService {

    @Autowired
    private BaseDao baseDao;

    @Override
    public boolean toProdOut(String jsons) throws IllegalAccessException {
        List<Map<Object, Object>> maps = JsonUtil.toMapList(jsons);
        List<String> insertSql = new ArrayList<String>();
        List<String> updateSql = new ArrayList<String>();
        int detno = 1;
        int tn_id = baseDao.getSeq("TOPWISECONNECT_seq");
        int id = baseDao.getSeq("ProdInOut_seq");
        String code = baseDao.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
        //记录上传数据
        Save(jsons, tn_id);
        //对接ERP
        for (Map<Object, Object> map : maps) {
            Object prcode = map.get("prcode");
            Object outwhcode = map.get("outwhcode");
            Object inwhcode = map.get("inwhcode");
            Object emname = map.get("emname");
            Date date = BaseUtil.parseStringToDate(map.get("date"), "yyyy-MM-dd");
            if (!StringUtils.isEmpty(prcode) && !StringUtils.isEmpty(outwhcode) && !StringUtils.isEmpty(inwhcode) && !StringUtils.isEmpty(emname)) {
                //检测必填项是否存在
                checkPrcode(prcode, tn_id);
                checkWarehouse(outwhcode, tn_id);
                checkWarehouse(inwhcode, tn_id);
                checkName(emname, tn_id);
                Object outname = baseDao.getFieldDataByCondition("warehouse", "WH_DESCRIPTION", "wh_code='" + outwhcode + "'");
                Object inname = baseDao.getFieldDataByCondition("warehouse", "WH_DESCRIPTION", "wh_code='" + inwhcode + "'");
                Object emcode = baseDao.getFieldDataByCondition("employee", "em_code", "em_name='" + emname + "'");
                //插入主表
                if (detno == 1) {
                    insertSql.add("insert into prodinout (pi_id,pi_inoutno,pi_class,pi_date,pi_type,pi_teamcode_user,pi_sourcecode," +
                            "pi_custname2,pi_ntbamount,pi_emname,pi_emcode,pi_departmentcode,pi_departmentname,pi_status,pi_invostatus,pi_printstatus,pi_recordman,pi_recorddate," +
                            "pi_invostatuscode,pi_statuscode,pi_whcode,pi_whname,pi_purpose,pi_purposename,pi_remark) select " + id + ",'" + code + "','拨出单',to_date('" + BaseUtil.parseDateToString(date, "yyyy-MM-dd") + "','yyyy-mm-dd')," +
                            "'库存转移',' ',' ',' ',0,'" + emname + "','" + emcode + "','15','售后部','未过账','在录入','未打印','" + emname + "',to_date('" + BaseUtil.parseDateToString(date, "yyyy-MM-dd") + "','yyyy-MM-dd')," +
                            "'ENTERING','UNPOST','" + outwhcode + "','" + outname + "','" + inwhcode + "','" + inname + "','售后系统' from dual");
                }
                //插入从表
                insertSql.add("insert into PRODIODETAIL (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_prodcode," +
                        "pd_outqty,pd_whcode,pd_whname,pd_inwhcode,pd_inwhname) select PRODIODETAIL_seq.nextval," + id + ",'" + code + "','拨出单'," + detno + ",'" + prcode + "'," +
                        "" + map.get("outnum") + ",'" + outwhcode + "','" + outname + "','"+ inwhcode +"','" + inname + "' from dual");
                detno ++;
            } else {
                baseDao.execute("update TOPWISECONNECT set tn_status=1 where tn_id=" + tn_id);
                throw new IllegalAccessException("必填项为空,插入失败");
            }

        }
        insertSql.add("update TOPWISECONNECT set tn_status=-1 where tn_id=" + tn_id);
        baseDao.execute(insertSql);
        return true;
    }

    private void checkName(Object name, int tn_id) throws  IllegalAccessException{
        int count = baseDao.getCount("select count(1) from employee where em_name=?", name);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=2 where tn_id=" + tn_id);
            throw new IllegalAccessException("erp接管人:" + name + " 在erp系统不存在,插入失败");
        }
    }

    private void checkPrcode(Object prcode, int tn_id) throws  IllegalAccessException {
        int count = baseDao.getCount("select count(1) from product where pr_code=?", prcode);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=3 where tn_id=" + tn_id);
            throw new IllegalAccessException("物料编号:" + prcode + " 在erp系统不存在,插入失败");
        }
    }

    private void checkWarehouse(Object warehouse, int tn_id) throws IllegalAccessException {
        int count = baseDao.getCount("select count(1) from warehouse where wh_code=?", warehouse);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=4 where tn_id=" + tn_id);
            throw new IllegalAccessException("仓库编号:" + warehouse + " 在erp系统不存在,插入失败");
        }
    }

    private void Save(String jsons, int id) {
        jsons = jsons.replace("'", "''");
        baseDao.execute("insert into TOPWISECONNECT select "+ id + ",'" + jsons + "',sysdate,0 from dual");
    }
}

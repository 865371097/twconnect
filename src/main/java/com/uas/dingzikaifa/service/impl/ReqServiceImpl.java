package com.uas.dingzikaifa.service.impl;

import com.uas.dingzikaifa.dao.BaseDao;
import com.uas.dingzikaifa.service.ReqService;
import com.uas.dingzikaifa.util.BaseUtil;
import com.uas.dingzikaifa.util.JsonUtil;
import com.uas.dingzikaifa.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Savepoint;
import java.util.*;

@Service
public class ReqServiceImpl implements ReqService {

    @Autowired
    private BaseDao baseDao;

    @Override
    public Map<String, Object> toProdOut(String jsons) throws IllegalAccessException {
        List<Map<Object, Object>> maps = JsonUtil.toMapList(jsons);
        List<String> insertSql = new ArrayList<String>();
        Map<String, Object> res = new HashMap<String, Object>();
        int detno = 1;
        int tn_id = baseDao.getSeq("TOPWISECONNECT_seq");
        int id = baseDao.getSeq("ProdInOut_seq");
        String code = baseDao.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
        String pi_code = null;//最终的拨出单单号
        //记录上传数据
        Save(jsons, tn_id);
        //对接ERP
        for (Map<Object, Object> map : maps) {
            Object prcode = map.get("prcode");
            Object outwhcode = map.get("outwhcode");
            Object inwhcode = map.get("inwhcode");
            Object emname = map.get("emname");
            Object remark = map.get("remark") == null ? "" : map.get("remark").toString().replace("'","''") ;
            Date date = BaseUtil.parseStringToDate(map.get("date"), "yyyy-MM-dd");
            if (!StringUtils.isEmpty(prcode) && !StringUtils.isEmpty(outwhcode) && !StringUtils.isEmpty(inwhcode) && !StringUtils.isEmpty(emname)) {
                //检测必填项是否存在
                String check = check(prcode, outwhcode, inwhcode, emname, tn_id);
                if (check != null) {
                    res.put("success", false);
                    res.put("result", check);
                    return res;
                }
                Object outname = baseDao.getFieldDataByCondition("warehouse", "WH_DESCRIPTION", "wh_code='" + outwhcode + "'");
                Object inname = baseDao.getFieldDataByCondition("warehouse", "WH_DESCRIPTION", "wh_code='" + inwhcode + "'");
                Object emcode = baseDao.getFieldDataByCondition("employee", "em_code", "em_name='" + emname + "'");
                //插入主表
                if (detno == 1) {
                    pi_code = code;
                    insertSql.add("insert into prodinout (pi_id,pi_inoutno,pi_class,pi_date,pi_type,pi_teamcode_user,pi_sourcecode," +
                            "pi_custname2,pi_ntbamount,pi_emname,pi_emcode,pi_departmentcode,pi_departmentname,pi_status,pi_invostatus,pi_printstatus,pi_recordman,pi_recorddate," +
                            "pi_invostatuscode,pi_statuscode,pi_whcode,pi_whname,pi_purpose,pi_purposename,pi_remark) select " + id + ",'" + code + "','拨出单',to_date('" + BaseUtil.parseDateToString(date, "yyyy-MM-dd") + "','yyyy-mm-dd')," +
                            "'库存转移',' ',' ',' ',0,'" + emname + "','" + emcode + "','15','售后部','未过账','在录入','未打印','" + emname + "',to_date('" + BaseUtil.parseDateToString(date, "yyyy-MM-dd") + "','yyyy-MM-dd')," +
                            "'ENTERING','UNPOST','" + outwhcode + "','" + outname + "','" + inwhcode + "','" + inname + "','" + remark + "' from dual");
                }
                //插入从表
                insertSql.add("insert into PRODIODETAIL (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_prodcode," +
                        "pd_outqty,pd_whcode,pd_whname,pd_inwhcode,pd_inwhname) select PRODIODETAIL_seq.nextval," + id + ",'" + code + "','拨出单'," + detno + ",'" + prcode + "'," +
                        "" + map.get("outnum") + ",'" + outwhcode + "','" + outname + "','"+ inwhcode +"','" + inname + "' from dual");
                detno ++;
            } else {
                baseDao.execute("update TOPWISECONNECT set tn_status=1 where tn_id=" + tn_id);res.put("success", false);
                res.put("result", "必填项为空,插入失败");
                return res;
            }

        }
        insertSql.add("update TOPWISECONNECT set tn_status=-1 where tn_id=" + tn_id);
        baseDao.execute(insertSql);
        res.put("success", pi_code == null ? false : true);
        res.put("result", pi_code == null ? "无效json" : pi_code);
        return res;
    }

    @Override
    public Map<String, Object> getWarehouse(String info) {
        Map<Object, Object> map = JsonUtil.toMap(info);
        Map<String, Object> resMap = new HashMap<String, Object>();
        String success = "success";
        String result = "";
        if (map != null) {
            Object whcode = map.get("wh_code");
            Object prcode = map.get("pr_code");
            if (!StringUtils.isEmpty(whcode) && !StringUtils.isEmpty(prcode)) {
                String[] codes = prcode.toString().split(",");
                StringBuffer con = new StringBuffer("pw_prodcode in ( ");
                StringBuffer res = new StringBuffer();
                for (String code : codes) {
                    con.append("'" + code + "',");
                }
                List<Object[]> datas = baseDao.getFieldsDatasByCondition("productwh", new String[]{"pw_onhand", "pw_prodcode"},
                        con.substring(0, con.length() - 1) + " ) and pw_whcode='" + whcode + "'");
                for (Object[] data : datas) {
                    res.append(data[1] + ":" + (data[0] == null ? 0 : data[0]) + ";");
                }
                //数据库没有该库存的显示物料编号 数据为0
                for (String pr_code : codes) {
                    boolean flag = false;
                    for (Object[] data : datas) {
                        if (pr_code.equals(data[1])) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        res.append(pr_code + ":0;");
                    }
                }
                result = res.toString().length() >0 ? res.toString().substring(0, res.length() - 1) : res.toString();
            }else {
                success = "fail";
                result = "json中库存编号或者物料编号不存在!";
            }
        }else {
            success = "fail";
            result = "json数据无法解析!";
        }

        resMap.put("success", success);
        resMap.put("result", result);
        return resMap;
    }

    private String check(Object prcode, Object outwhcode, Object inwhcode, Object emname, int tn_id) {
        int count = 0;
         count = baseDao.getCount("select count(1) from product where pr_code=?", prcode);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=3 where tn_id=" + tn_id);
            return "物料编号:" + prcode + " 在erp系统不存在,插入失败";
        }
        count = baseDao.getCount("select count(1) from warehouse where wh_code=?", outwhcode);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=4 where tn_id=" + tn_id);
            return "拨出仓编号:" + outwhcode + " 在erp系统不存在,插入失败";
        }
        count = baseDao.getCount("select count(1) from warehouse where wh_code=?", inwhcode);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=4 where tn_id=" + tn_id);
            return "拨入仓编号:" + inwhcode + " 在erp系统不存在,插入失败";
        }
        count = baseDao.getCount("select count(1) from employee where em_name=?", emname);
        if (count != 1) {
            baseDao.execute("update TOPWISECONNECT set tn_status=2 where tn_id=" + tn_id);
            return "erp接管人:" + emname + " 在erp系统不存在,插入失败";
        }
        return null;
    }

    private void Save(String jsons, int id) {
        jsons = jsons.replace("'", "''");
        baseDao.execute("declare v_colb clob; begin  v_colb:='" + jsons + "'; " +
                "insert into TOPWISECONNECT values (" + id + " , v_colb, sysdate, 0); end; ");
    }
}

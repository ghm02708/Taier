package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.batch.bo.datasource.DsTypeVersionParam;
import com.dtstack.batch.vo.datasource.DsFormFieldVo;
import com.dtstack.batch.vo.datasource.DsFormTemplateVo;
import com.dtstack.engine.domain.datasource.DsFormField;
import com.dtstack.engine.mapper.datasource.DsFormFieldMapper;
import dt.insight.plat.lang.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
@Slf4j
@Service
public class DsFormFieldService extends BaseService<DsFormFieldMapper, DsFormField> {

    private static String COMMON = "common";


    /**
     * 根据数据库类型和版本查找表单模版
     * @param param
     * @return
     */
    public DsFormTemplateVo findTemplateByTypeVersion(DsTypeVersionParam param) {
        DsFormTemplateVo returnVo = new DsFormTemplateVo();
        String typeVersion = param.getDataType();
        if (Strings.isNotBlank(param.getDataVersion())) {
            typeVersion = param.getDataType() + "-" + param.getDataVersion();
        }
        List<DsFormField> formFieldList = this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).
                or().eq("type_version", COMMON));
        List<DsFormFieldVo> formFieldVos = new ArrayList<>();
        for (DsFormField dsFormField : formFieldList) {
            DsFormFieldVo dsFormFieldVo = new DsFormFieldVo();
            BeanUtils.copyProperties(dsFormField,dsFormFieldVo);
            if(StringUtils.isNotBlank(dsFormField.getOptions())){
                List<Map> optionList = JSON.parseArray(dsFormField.getOptions(), Map.class);
                dsFormFieldVo.setOptions(optionList);
            }
            formFieldVos.add(dsFormFieldVo);
        }
        returnVo.setDataType(param.getDataType());
        returnVo.setDataVersion(param.getDataVersion());
        returnVo.setFromFieldVoList(formFieldVos);
        return returnVo;
    }

    /**
     * 根据数据源类型和版本获取具有连接性质的属性列表
     * @param dataType
     * @param dataVersion
     * @return
     */
    public List<DsFormField> findLinkFieldByTypeVersion(String dataType, String dataVersion) {
        String typeVersion = dataType;
        if (Strings.isNotBlank(dataVersion)) {
            typeVersion = dataType + "-" + dataVersion;
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).eq("is_link", 1));
    }
}

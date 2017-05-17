package net.myspring.future.modules.crm.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.modules.sys.dto.DictEnumDto;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.mapper.ProductMapper;
import net.myspring.future.modules.crm.domain.ProductIme;
import net.myspring.future.modules.crm.dto.ProductImeDto;
import net.myspring.future.modules.crm.mapper.ProductImeMapper;
import net.myspring.future.modules.crm.mapper.ProductImeSaleMapper;
import net.myspring.future.modules.crm.mapper.ProductImeUploadMapper;
import net.myspring.future.modules.crm.web.query.ProductImeQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ProductImeService {
    @Autowired
    private ProductImeMapper productImeMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CacheUtils cacheUtils;

    //分页，但不查询总数
    public Page<ProductImeDto> findPage(Pageable pageable,ProductImeQuery productImeQuery) {
        productImeQuery.setPageable(pageable);
        List<ProductIme> productImeList = productImeMapper.findList(productImeQuery);
        List<ProductImeDto> productImeDtoList= BeanUtil.map(productImeList,ProductImeDto.class);
        cacheUtils.initCacheInput(productImeDtoList);
        Page<ProductImeDto> page = new PageImpl(productImeList,pageable,(pageable.getPageNumber()+100)*pageable.getPageSize());
        return page;
    }


    public List<ProductImeDto> findByImeList(List<String> imeList){
        List<ProductIme> productImeList=productImeMapper.findByImeList(imeList);
        List<ProductImeDto> productImeDtoList= BeanUtil.map(productImeList,ProductImeDto.class);
        cacheUtils.initCacheInput(productImeDtoList);
        return productImeDtoList;
    }

    public Map<String,Integer> findQtyMap(List<String> imeList){
        Map<String,Integer> map= Maps.newHashMap();
        List<ProductIme> productImeList=productImeMapper.findByImeList(imeList);
        if(CollectionUtil.isNotEmpty(productImeList)){
            List<Product> productList=productMapper.findByIds(CollectionUtil.extractToList(productImeList,"productId"));
            Map<String,Product> productMap=CollectionUtil.extractToMap(productList,"id");
            Map<String,List<ProductIme>> productImeMap=CollectionUtil.extractToMapList(productImeList,"productId");
            for(Map.Entry<String,List<ProductIme>> entry:productImeMap.entrySet()){
                map.put(productMap.get(entry.getKey()).getName(),entry.getValue().size());
            }
        }
        return map;
    }
}

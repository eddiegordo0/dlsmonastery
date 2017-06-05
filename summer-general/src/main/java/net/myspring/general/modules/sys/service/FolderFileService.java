package net.myspring.general.modules.sys.service;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import net.myspring.general.common.utils.CacheUtils;
import net.myspring.general.common.utils.RequestUtils;
import net.myspring.general.modules.sys.domain.FolderFile;
import net.myspring.general.modules.sys.dto.FolderFileDto;
import net.myspring.general.modules.sys.repository.FolderFileRepository;
import net.myspring.general.modules.sys.web.query.FolderFileQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FolderFileService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FolderFileRepository folderFileRepository;
    @Autowired
    private GridFsTemplate storageGridFsTemplate;
    @Autowired
    private GridFsTemplate previewGridFsTemplate;
    @Autowired
    private GridFsTemplate tempGridFsTemplate;
    @Autowired
    private CacheUtils cacheUtils;

    public List<FolderFileDto> save(String folderId, Map<String, MultipartFile> fileMap) {
        DBObject dbObject = RequestUtils.getDbObject();
        List<FolderFile> list = Lists.newArrayList();
        try {
            for (MultipartFile multipartFile : fileMap.values()) {
                if (multipartFile.getSize() > 0) {
                    //保存到mongoDb
                    GridFSFile gridFSFile = storageGridFsTemplate.store(multipartFile.getInputStream(),multipartFile.getOriginalFilename(),multipartFile.getContentType(),dbObject);
                    GridFSFile preview = null;
                    //如果是图片类型
                    if(multipartFile.getContentType().startsWith("image/")) {
                        BufferedImage image = Scalr.resize(ImageIO.read(multipartFile.getInputStream()), 290);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        preview = previewGridFsTemplate.store(new ByteArrayInputStream(baos.toByteArray()),multipartFile.getOriginalFilename(),multipartFile.getContentType(),dbObject);
                    }
                    // 保存到数据库
                    FolderFile folderFile = new FolderFile();
                    folderFile.setFolderId(folderId);
                    folderFile.setContentType(multipartFile.getContentType());
                    folderFile.setName(multipartFile.getOriginalFilename().replaceAll("/","."));
                    folderFile.setSize(Integer.valueOf(String.valueOf(multipartFile.getSize())));
                    folderFile.setPhysicalName(folderFile.getName());
                    folderFile.setMongoId(StringUtils.toString(gridFSFile.getId()));
                    if(preview != null) {
                        folderFile.setMongoPreviewId(StringUtils.toString(preview.getId()));
                    }
                    folderFile.setCompanyId(RequestUtils.getCompanyId());
                    folderFileRepository.save(folderFile);
                    list.add(folderFile);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return BeanUtil.map(list,FolderFileDto.class);
    }

    public GridFSDBFile getGridFSDBFile(String type,String mongoId) {
        GridFsTemplate gridFsTemplate;
        if("storage".equals(type)) {
            gridFsTemplate = storageGridFsTemplate;
        } else  if("preview".equals(type)) {
            gridFsTemplate = previewGridFsTemplate;
        } else {
            gridFsTemplate = tempGridFsTemplate;
        }
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(mongoId)));
    }

    @Transactional(readOnly = true)
    public FolderFile findOne(String id) {
        FolderFile folderFile =  folderFileRepository.findOne(id);
        return folderFile;
    }


    @Transactional(readOnly = true)
    public List<FolderFileDto> findByIds(List<String> ids) {
        if(CollectionUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        List<FolderFile> folderFileList =  folderFileRepository.findAll(ids);
        return BeanUtil.map(folderFileList,FolderFileDto.class);
    }

    @Transactional(readOnly = true)
    public Page<FolderFileDto> findPage(Pageable pageable, FolderFileQuery folderFileQuery) {
        Page<FolderFileDto> folderFilePage= folderFileRepository.findPage(pageable,folderFileQuery);
        cacheUtils.initCacheInput(folderFilePage.getContent());
        return folderFilePage;
    }
}

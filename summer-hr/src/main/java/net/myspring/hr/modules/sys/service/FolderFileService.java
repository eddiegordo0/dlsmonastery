package net.myspring.hr.modules.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import net.myspring.hr.modules.sys.mapper.FolderFileMapper;

@Service
public class FolderFileService {

    @Autowired
    private FolderFileMapper folderFileMapper;

}

package com.xhzb.nursing.service.impl;

import java.io.InputStream;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.xhzb.common.exception.base.BaseException;
import com.xhzb.common.utils.DateUtils;
import com.xhzb.oss.client.OSSAliyunFileStorageService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import com.xhzb.nursing.mapper.KnowledgeBaseMapper;
import com.xhzb.nursing.domain.KnowledgeBase;
import com.xhzb.nursing.service.IKnowledgeBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;

/**
 * 知识库主Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-13
 */
@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> implements IKnowledgeBaseService
{
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    /**
     * 查询知识库主
     * 
     * @param id 知识库主主键
     * @return 知识库主
     */
    @Override
    public KnowledgeBase selectKnowledgeBaseById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询知识库主列表
     * 
     * @param knowledgeBase 知识库主
     * @return 知识库主
     */
    @Override
    public List<KnowledgeBase> selectKnowledgeBaseList(KnowledgeBase knowledgeBase)
    {
        return knowledgeBaseMapper.selectKnowledgeBaseList(knowledgeBase);
    }


    @Autowired
    private OSSAliyunFileStorageService ossAliyunFileStorageService;

    @Autowired
    private TextSplitter textSplitter;

    @Autowired
    private VectorStore   vectorStore;
    /**
     * 新增知识库主
     * 
     * @param knowledgeBase 知识库主
     * @return 结果
     */
    @Override
    public int insertKnowledgeBase(KnowledgeBase knowledgeBase)
    {
//        下载文件
        InputStream inputStream = ossAliyunFileStorageService.download(knowledgeBase.getDocumentUrl());
        InputStreamResource resource = new InputStreamResource(inputStream);

//        读取PDF文件
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0) // 设置页眉边距
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .build())
                        .withPagesPerDocument(1) // 每个文档的页数
                        .build());
//        对PDF拆分
        List<Document> documentList = textSplitter.split(pdfReader.read());
//        获取所有文档的ID
        List<String> documentIds = documentList.stream().map(d -> d.getId()).toList();
//        分批次 写入向量数据库
        //存储到向量数据库中，分批添加，不然会报错
        for (int i = 0; i < documentList.size(); i += 10) {
            List<Document> list = documentList.subList(i, Math.min(i + 10, documentList.size()));
            vectorStore.add(list);
        }
//        保存 mysql 知识库 信息
        knowledgeBase.setRemark(JSONUtil.toJsonStr(documentIds));
        knowledgeBase.setCreateTime(DateUtils.getNowDate());
        return  knowledgeBaseMapper.insertKnowledgeBase(knowledgeBase);
        //return save(knowledgeBase)? 1 : 0;
    }

    /**
     * 修改知识库主
     * 
     * @param knowledgeBase 知识库主
     * @return 结果
     */
    @Override
    public int updateKnowledgeBase(KnowledgeBase knowledgeBase)
    {
        return updateById(knowledgeBase)? 1 : 0;
    }

    /**
     * 批量删除知识库主
     * 
     * @param ids 需要删除的知识库主主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeBaseByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))? 1 : 0;
    }

    /**
     * 删除知识库主信息
     * 
     * @param id 知识库主主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeBaseById(Long id)
    {
        // 删除向量库
        KnowledgeBase knowledgeBase = selectKnowledgeBaseById(id);
        if(knowledgeBase == null){
            throw  new BaseException("知识库不存在");
        }
        String idsStr = knowledgeBase.getRemark();
        List<String> ids = JSONUtil.toList(idsStr, String.class);
        vectorStore.delete(ids);
        //删除oss
        ossAliyunFileStorageService.delete(knowledgeBase.getDocumentUrl());
        // 删除mysql
        return knowledgeBaseMapper.deleteKnowledgeBaseById(id);
    }
}

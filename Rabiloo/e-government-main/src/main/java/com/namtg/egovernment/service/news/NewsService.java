package com.namtg.egovernment.service.news;

import com.github.slugify.Slugify;
import com.namtg.egovernment.dto.news.NewsDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.news.NewsEntity;
import com.namtg.egovernment.repository.news.NewsRepository;
import com.namtg.egovernment.service.AmazonService;
import com.namtg.egovernment.service.DownloadFileService;
import com.namtg.egovernment.service.field.FieldService;
import com.namtg.egovernment.service.field.SubFieldService;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private SubFieldService subFieldService;

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private DownloadFileService downloadFileService;

    public Page<NewsEntity> getPage(String keyword, Pageable pageable) {
        Page<NewsEntity> pageResult = newsRepository.getPage(keyword, pageable);
        List<Long> listSubFieldId = pageResult
                .stream()
                .map(NewsEntity::getSubFieldId)
                .collect(Collectors.toList());
        Map<Long, String> mapSubFieldNameBySubFieldId = subFieldService.getMapSubFieldNameBySubFieldId(listSubFieldId);
        Map<Long, String> mapFieldNameBySubFieldId = fieldService.getMapFieldNameBySubFieldId(listSubFieldId);

        pageResult.getContent().forEach(news -> {
            news.setSubFieldName(mapSubFieldNameBySubFieldId.get(news.getSubFieldId()));
            news.setFieldName(mapFieldNameBySubFieldId.get(news.getSubFieldId()));
        });
        return pageResult;
    }

    public ServerResponseDto saveNews(NewsDto newsDto, Long creatorId) {
        Long newsId = newsDto.getId();
        boolean isUpdate = newsId != null;
        NewsEntity newsEntity;
        boolean isNewsExist;
        if (isUpdate) {
            newsEntity = newsRepository.findByIdAndIsDeletedFalse(newsId);
            isNewsExist = isNewsExist(newsDto.getTitle(), newsEntity.getId());
            newsEntity.setUpdatedByUserId(creatorId);
        } else {
            isNewsExist = isNewsExist(newsDto.getTitle());
            newsEntity = new NewsEntity();
            newsEntity.setCreatedTime(new Date());
            newsEntity.setCreatedByUserId(creatorId);
        }
        if (isNewsExist) {
            return new ServerResponseDto(ResponseCase.NEWS_EXIST);
        }
        newsEntity.setUpdatedTime(new Date());
        newsEntity.setSubFieldId(newsDto.getSubFieldId());
        newsEntity.setTitle(newsDto.getTitle());
        newsEntity.setSeo(new Slugify().slugify(newsDto.getTitle()));
        newsEntity.setContent(newsDto.getContent());

        MultipartFile image = newsDto.getImage();
        if (image != null) {
            String nameImage = image.getOriginalFilename();
            newsEntity.setNameImage(nameImage);
            String urlImage = amazonService.uploadFile(image);
            newsEntity.setUrlImage(urlImage);
        }

        newsRepository.save(newsEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    private boolean isNewsExist(String title) {
        return newsRepository.countNewsExist(title) > 0;
    }

    private boolean isNewsExist(String title, Long newsId) {
        return newsRepository.countNewsExist(title, newsId) > 0;
    }

    public ServerResponseDto detail(Long newsId) {
        NewsEntity newsEntity = newsRepository.findByIdAndIsDeletedFalse(newsId);
        Long fieldId = subFieldService.getFieldIdBySubFieldId(newsEntity.getSubFieldId());
        newsEntity.setFieldId(fieldId);
        return new ServerResponseDto(ResponseCase.SUCCESS, newsEntity);
    }

    public ServerResponseDto delete(Long newsId) {
        NewsEntity newsEntity = newsRepository.findByIdAndIsDeletedFalse(newsId);
        if (newsEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        newsEntity.setDeleted(true);
        newsRepository.save(newsEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public NewsEntity getNewsLatest() {
        return newsRepository.getNewsLatest();
    }

    public List<NewsEntity> getListNews() {
        List<NewsEntity> listNews = newsRepository.getListNews();
        setContentAfterParseHtml(listNews);
        return listNews;
    }

    private void setContentAfterParseHtml(List<NewsEntity> listNews) {
        listNews.forEach(news -> {
            news.setContentParse(Jsoup.parse(news.getContent()).text());
        });
    }

    @Transactional
    public void deleteByListSubFieldId(List<Long> listSubFieldIdBeDeleted) {
        newsRepository.deleteByListSubFieldId(listSubFieldIdBeDeleted);
    }

    public List<NewsEntity> getListTop5NewsLatest() {
        return newsRepository.getListTop5NewsLatest();
    }

    public NewsEntity getBySeo(String seo) {
        return newsRepository.findBySeoAndIsDeletedFalse(seo);
    }

    public void downloadNews(Long newsId, HttpServletResponse response) throws IOException {
        NewsEntity newsEntity = newsRepository.findByIdAndIsDeletedFalse(newsId);

        XWPFDocument document = new XWPFDocument();
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(newsEntity.getTitle());
        titleRun.setColor("000000");
        titleRun.setBold(true);
        titleRun.setFontFamily("Times New Roman");
        titleRun.setFontSize(20);

        XWPFParagraph content = document.createParagraph();
        XWPFRun contentRun = content.createRun();
        contentRun.setText(newsEntity.getContent());
        contentRun.setColor("000000");
        contentRun.setFontFamily("Times New Roman");
        contentRun.setFontSize(13);


        String fileName = "news.doc";
        try {
            response.setContentType("application/msword; charset=UTF-8");
            response.setHeader("Content-disposition", "attachment; fileName=\"" + fileName + "\"");

            document.write(response.getOutputStream());
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

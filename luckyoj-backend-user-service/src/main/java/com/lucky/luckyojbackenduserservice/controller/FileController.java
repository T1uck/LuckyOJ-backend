package com.lucky.luckyojbackenduserservice.controller;


import com.lucky.luckyojbackendcommon.common.BaseResponse;
import com.lucky.luckyojbackendcommon.common.ResultUtils;
import com.lucky.luckyojbackenduserservice.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    private static final String FILE_DELIMITER = ",";


    @Resource
    private CosManager cosManager;

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, Object>> uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>(2);
        // 上传并返回新文件名称
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String fileName = uuid + "-" + file.getOriginalFilename();
        String imageUrl = cosManager.uploadFile2OSS(file.getInputStream(), fileName);
        result.put("url", imageUrl);
        return ResultUtils.success(result);
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public BaseResponse<Map<String, Object>> uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 上传文件路径
//            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String uuid = RandomStringUtils.randomAlphanumeric(8);
                String fileName = uuid + "-" + file.getOriginalFilename();
                String imageUrl = cosManager.uploadFile2OSS(file.getInputStream(), fileName);
                urls.add(imageUrl);
                fileNames.add(fileName);
//                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            Map<String, Object> result = new HashMap<>();
            result.put("urls", StringUtils.join(urls, FILE_DELIMITER));
            result.put("fileNames", StringUtils.join(fileNames, FILE_DELIMITER));
//            result.put("newFileNames" , StringUtils.join(newFileNames, FILE_DELIMITER));
            result.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMITER));
            return ResultUtils.success(result);
        } catch (Exception e) {
            throw new Exception("上传文件失败");
        }
    }


//    /**
//     * 文件上传
//     *
//     * @param multipartFile
//     * @param uploadFileRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/upload")
//    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
//            UploadFileRequest uploadFileRequest, HttpServletRequest request) {
//        String biz = uploadFileRequest.getBiz();
//        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
//        if (fileUploadBizEnum == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        validFile(multipartFile, fileUploadBizEnum);
//        User loginUser = userService.getLoginUser(request);
//        // 文件目录：根据业务、用户来划分
//        String uuid = RandomStringUtils.randomAlphanumeric(8);
//        String filename = uuid + "-" + multipartFile.getOriginalFilename();
//        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
//        File file = null;
//        try {
//            // 上传文件
//            file = File.createTempFile(filepath, null);
//            multipartFile.transferTo(file);
//            cosManager.putObject(filepath, file);
//            // 返回可访问地址
//            return ResultUtils.success(FileConstant.COS_HOST + filepath);
//        } catch (Exception e) {
//            log.error("file upload error, filepath = " + filepath, e);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
//        } finally {
//            if (file != null) {
//                // 删除临时文件
//                boolean delete = file.delete();
//                if (!delete) {
//                    log.error("file delete error, filepath = {}", filepath);
//                }
//            }
//        }
//    }
//
//    /**
//     * 校验文件
//     *
//     * @param multipartFile
//     * @param fileUploadBizEnum 业务类型
//     */
//    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
//        // 文件大小
//        long fileSize = multipartFile.getSize();
//        // 文件后缀
//        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
//        final long ONE_M = 1024 * 1024L;
//        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
//            if (fileSize > ONE_M) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
//            }
//            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
//            }
//        }
//    }
}

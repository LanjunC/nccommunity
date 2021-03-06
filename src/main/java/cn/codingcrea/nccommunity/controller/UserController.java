package cn.codingcrea.nccommunity.controller;

import cn.codingcrea.nccommunity.annotation.LoginRequired;
import cn.codingcrea.nccommunity.entity.User;
import cn.codingcrea.nccommunity.service.FollowService;
import cn.codingcrea.nccommunity.service.LikeService;
import cn.codingcrea.nccommunity.service.UserService;
import cn.codingcrea.nccommunity.util.CommunityConstant;
import cn.codingcrea.nccommunity.util.HostHolder;
import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${nccommunity.path.upload}")
    private String uploadPath;

    @Value("${nccommunity.path.domain}")
    private String domain;

    @Value(("${server.servlet.context-path}"))
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    private String keyid;

    @Value("${aliyun.oss.file.secretid}")
    private String secretid;

    @Value("${aliyun.oss.file.bucket.name}")
    private String bucket;

    @Value("${aliyun.oss.file.bucket.dir}")
    private String dirName;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * ????????????(??????????????????)
     * @param headerimage mvc???????????????API
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeaderLocal(MultipartFile headerimage, Model model) {
        if(headerimage == null) {
            model.addAttribute("error", "?????????????????????");
            return "/site/setting";
        }


        String filename = headerimage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));  //.png?????????
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        //??????????????????url
        filename = NcCommunityUtil.generateUUID() + suffix;

        //????????????
        File file = new File(uploadPath + "/" + filename);
        try {
            headerimage.transferTo(file);
        } catch (IOException e) {
            logger.error("??????????????????:" + e.getMessage());
            throw new RuntimeException("???????????????????????????????????????", e);
        }

        //??????headerUrl(web????????????)
        //http://localhost:8080/nccommunity/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * ???????????????Oss,https://blog.csdn.net/z1c5809145294zv/article/details/
     * 106674169?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-1&spm=1001.2101.3001.4242
     * @param headerimage
     * @param model
     * @return
     */
    @RequestMapping(value = "/uploadoss", method = RequestMethod.POST)
    public String uploadHeaderOss(MultipartFile headerimage, Model model) {
        if(headerimage == null) {
            model.addAttribute("error", "?????????????????????");
            return "/site/setting";
        }


        String filename = headerimage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));  //.png?????????
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        //??????????????????url
        filename = NcCommunityUtil.generateUUID() + suffix;

        //???????????????????????????????????????
        filename = dirName + "/" + filename;

        //????????????
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, secretid);
        try(
                InputStream is = headerimage.getInputStream();
                ) {
            ossClient.putObject(bucket, filename, is);
            ossClient.shutdown();
        } catch (IOException e) {
            logger.error("??????????????????:" + e.getMessage());
            throw new RuntimeException("???????????????????????????????????????", e);
        }

        //??????headerUrl(web????????????)
        //????????????(web????????????)http://${bucket}.oss-cn-shanghai.aliyuncs.com/${dirName}/${filename}
        User user = hostHolder.getUser();
        String headerUrl = "http://" + bucket + "." + endpoint + "/" + filename;        //?????????????????????????????????
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }


    /**
     * ??????????????????????????????
     * @param filename
     * @param response
     */
    @RequestMapping(path = "/headerlocal/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {

        filename = uploadPath + "/" + filename;        //?????????????????????????????????
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
//        System.out.println(suffix);

        //????????????????????????????????????io??????????????????????????????ImageIO.write(image,"png",os);????????????????????????????????????
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer =new byte[1024];
            int b = 0;
            while((b=fis.read(buffer))!= -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("??????????????????:" + e.getMessage());
        }
    }



    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if(user==null) {
            throw new RuntimeException("?????????????????????");
        }

        //??????
        model.addAttribute("user", user);
        //??????????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //????????????????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        //?????????????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //???????????????
        boolean hasFollowed = false;
        //??????????????????
        if(hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }


}

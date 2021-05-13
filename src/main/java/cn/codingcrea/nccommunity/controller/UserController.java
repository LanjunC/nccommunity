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
     * 更新头像(本地，已废弃)
     * @param headerimage mvc框架提供的API
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeaderLocal(MultipartFile headerimage, Model model) {
        if(headerimage == null) {
            model.addAttribute("error", "您未选择图片！");
            return "/site/setting";
        }


        String filename = headerimage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));  //.png等后缀
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        //生成随机访问url
        filename = NcCommunityUtil.generateUUID() + suffix;

        //上传头像
        File file = new File(uploadPath + "/" + filename);
        try {
            headerimage.transferTo(file);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！", e);
        }

        //更新headerUrl(web访问路径)
        //http://localhost:8080/nccommunity/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 更新头像到Oss,https://blog.csdn.net/z1c5809145294zv/article/details/
     * 106674169?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-1&spm=1001.2101.3001.4242
     * @param headerimage
     * @param model
     * @return
     */
    @RequestMapping(value = "/uploadoss", method = RequestMethod.POST)
    public String uploadHeaderOss(MultipartFile headerimage, Model model) {
        if(headerimage == null) {
            model.addAttribute("error", "您未选择图片！");
            return "/site/setting";
        }


        String filename = headerimage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));  //.png等后缀
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        //生成随机访问url
        filename = NcCommunityUtil.generateUUID() + suffix;

        //文件名加上头像所在文件夹名
        filename = dirName + "/" + filename;

        //上传头像
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, secretid);
        try(
                InputStream is = headerimage.getInputStream();
                ) {
            ossClient.putObject(bucket, filename, is);
            ossClient.shutdown();
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！", e);
        }

        //更新headerUrl(web访问路径)
        //文件位置(web访问路径)http://${bucket}.oss-cn-shanghai.aliyuncs.com/${dirName}/${filename}
        User user = hostHolder.getUser();
        String headerUrl = "http://" + bucket + "." + endpoint + "/" + filename;        //服务器实际存放头像位置
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }


    /**
     * 访问本地头像（废弃）
     * @param filename
     * @param response
     */
    @RequestMapping(path = "/headerlocal/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {

        filename = uploadPath + "/" + filename;        //服务器实际存放头像位置
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
//        System.out.println(suffix);

        //注意这里只能用传统的文件io方法而不能用验证码的ImageIO.write(image,"png",os);因为第一个参数类型不满足
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
            logger.error("读取头像失败:" + e.getMessage());
        }
    }



    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if(user==null) {
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user", user);
        //用户获得的赞
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //用户关注的人数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        //用户粉丝的数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //是否已关注
        boolean hasFollowed = false;
        //登录了才检索
        if(hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }


}

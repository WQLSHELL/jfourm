package com.controller;

import com.alibaba.fastjson.JSON;
import com.model.Comment;
import com.model.Question;
import com.model.User;
import com.service.CommentService;
import com.service.QuestionService;
import com.system.web.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.sql.Timestamp;

@Controller
@RequestMapping("/comment")
@SessionAttributes(names = {"user"})
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private QuestionService questionService;

    /* 给评论点赞 */
    @RequestMapping("/likeComment.action")
    @ResponseBody
    public String likeQuestion(Comment comment) {
        commentService.addLikeNum(comment);
        return JSON.toJSONString(response);
    }

    /* 添加评论 */
    @RequestMapping("/addComment.action")
    @ResponseBody
    public String addComment(Integer questionId, String content, ModelMap modelMap) {
        User user = (User) modelMap.get("user");
        Question question = questionService.get(questionId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setLikeNum(0);
        comment.setSubmitTime(new Timestamp(System.currentTimeMillis()));
        comment.setUser(user);
        comment.setQuestion(question);
        commentService.save(comment);

        response.setStatus(true);
        response.setMessage("评论成功.");
        return JSON.toJSONString(response);
    }

    /* 我的评论 */
    @RequestMapping("/listMyComment.action")
    public String listMyQuestion(@RequestParam(name = "pageNo", required = false) Integer pageNo, ModelMap modelMap) {
        Page<Comment> page;
        if (pageNo == null) {
            page = new Page<>(1);
        } else {
            page = new Page<>(pageNo);
        }
        User user = (User) modelMap.get("user");
        page = commentService.listMyAnswers(user, page);
        modelMap.addAttribute("page", page);
        return "/user/profile/my_answer";
    }

    /* 删除评论 */
    @RequestMapping("/deleteComment.action")
    @ResponseBody
    public String deleteComment (Integer commentId){
        Comment comment = commentService.get(commentId);
        commentService.delete(comment);
        return "success";
    }

}

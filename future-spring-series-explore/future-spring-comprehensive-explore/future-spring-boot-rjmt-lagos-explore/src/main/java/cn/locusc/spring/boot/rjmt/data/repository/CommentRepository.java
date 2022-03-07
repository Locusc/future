package cn.locusc.spring.boot.rjmt.data.repository;

import cn.locusc.spring.boot.rjmt.data.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
}

package com.example.domain.repositoty;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Comment;


/**
 * @author nhson
 *
 */
@Repository
public class CommentRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate template;

	private final static String COMMENT_TABLE = "comments";
	
	/**
	 * 新しい記事を追加するメソッドです.
	 * 
	 * @param comment コメント情報
	 */
	public void insert(Comment comment) {
		
		String sql = "Insert Into " + COMMENT_TABLE + " (name, content,article_id) values (:name,:content,:article_id);";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", comment.getName()).addValue("content", comment.getContent()).addValue("article_id", comment.getArticleId());
		template.update(sql, param);
	}	
	
	public List<Comment> findAll() {
		
		//will be implemented later
		return null;
	}
	
	public void deleteByArticleId(int i) {
		//will be implemented later
	}
}

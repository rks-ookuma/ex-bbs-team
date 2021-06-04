package com.example.repository;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Article;
import com.example.domain.Comment;

/**
 * 記事テーブルを操作するリポジトリ.
 *
 * @author takahiro.okuma
 *
 */
@Repository
public class ArticleRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	private static final String TABLE_ARTICLES = "articles";

	/** 記事テーブルと集約関係の子にあたるコメントテーブル名 */
	private static final String TABLE_COMMENTS = "comments";

	private static final ResultSetExtractor<List<Article>> ARTICLE_ROW_MAPPER = (rs) -> {

		List<Article> articleList = new LinkedList<>();
		Article article = null;
		List<Comment> commentList = null;

		int beforeId = -1;
		int nowId = 0;

		while (rs.next()) {

			nowId = rs.getInt("articles_id");

			if (nowId != beforeId) {
				article = new Article();
				article.setId(rs.getInt("articles_id"));
				article.setName(rs.getString("articles_name"));
				article.setContent(rs.getString("articles_content"));
				commentList = new LinkedList<>();
				article.setCommentList(commentList);
				articleList.add(article);
			}

			if (rs.getString("comments_name") != null) {

				Comment comment = new Comment();
				comment.setId(rs.getInt("comments_id"));
				comment.setName(rs.getString("comments_name"));
				comment.setContent(rs.getString("comments_content"));
				comment.setArticleId(rs.getInt("comments_article_id"));
				commentList.add(comment);
			}

			beforeId = nowId;
		}

		return articleList;
	};

	/**
	 * 記事を全件検索する.
	 *
	 * @return 全記事のドメインがIDの降順（新しい順）に入ったリスト ※コメントリストはIDの昇順（古い順）
	 */
	public List<Article> findAll() {
		String sql = "SELECT a.id AS articles_id, a.name AS articles_name, a.content AS articles_content, c.id AS comments_id, c.name AS comments_name, c.content AS comments_content, c.article_id AS comments_article_id"
				+ " FROM " + TABLE_ARTICLES + " a LEFT OUTER JOIN " + TABLE_COMMENTS
				+ " c ON a.id=c.article_id ORDER BY a.id DESC, c.id ASC;";
		List<Article> articleList = template.query(sql, ARTICLE_ROW_MAPPER);
		return articleList;

	}

	/**
	 * 記事を新規登録する.
	 *
	 * @param article 新たに登録したい記事ドメイン
	 */
	public void insert(Article article) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(article);
		String sql = "INSERT INTO " + TABLE_ARTICLES + " (name,content) VALUES (:name, :content);";
		template.update(sql, param);
	}

	/**
	 * 指定したIDの記事をDBから削除する. その記事についたコメントもDBのコメントテーブルから削除される。
	 *
	 * @param id 削除したい記事のID
	 */
	public void deleteById(int id) {
//		String sql = "DELETE FROM " + TABLE_ARTICLES + " WHERE id=:id;";
//		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
//		template.update(sql, param);

		// 案②：WITHとRETURNINGを使った外部制約付きの一括削除 ⇒CREATE文をいじれなかったとき
		String sql = "WITH deleted_articles AS (DELETE FROM " + TABLE_ARTICLES + " WHERE id=:id RETURNING id )"
				+ " DELETE FROM " + TABLE_COMMENTS + " WHERE article_id IN (SELECT id FROM deleted_articles);";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);

	}

}

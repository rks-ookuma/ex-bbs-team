package com.example.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 記事を投稿する際に利用されるフォーム.
 *
 * @author takahiro.okuma
 *
 */
public class ArticleForm {

	/** 投稿者名 */
	@NotBlank(message = "投稿者名を入力してください")
	@Size(min = 0, max = 50, message = "名前は50字以内で入力してください")
	private String name;
	/** 投稿内容 */
	@NotBlank(message = "投稿内容を入力してください")
	private String content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ArticleInsertForm [name=" + name + ", content=" + content + "]";
	}

}

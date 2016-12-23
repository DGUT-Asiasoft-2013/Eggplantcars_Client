package entity;

import java.io.Serializable;
import java.util.Date;

public class Concern implements Serializable{



	public static class Key implements Serializable{
		User user;
		User news_author;	
		
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public User getNews_author() {
			return news_author;
		}
		public void setNews_author(User news_author) {
			this.news_author = news_author;
		}
	}

	Key idKey;

	Date createDate;


	public Key getIdKey() {
		return idKey;
	}

	public void setIdKey(Key idKey) {
		this.idKey = idKey;
	}

	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}

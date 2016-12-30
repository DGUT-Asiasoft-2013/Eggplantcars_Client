package entity;

import java.io.Serializable;
import java.util.Date;



public class Record implements Serializable{
	
	int id;
	User user;//交易用户
	String record_type;//交易类型
	Date createDate;//交易时间
	String text;//备注
	int record_cash;//交易金额
	int my_cash;//我的余额
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getRecord_type() {
		return record_type;
	}
	public void setRecord_type(String record_type) {
		this.record_type = record_type;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getRecord_cash() {
		return record_cash;
	}
	public void setRecord_cash(int record_cash) {
		this.record_cash = record_cash;
	}
	public int getMy_cash() {
		return my_cash;
	}
	public void setMy_cash(int my_cash) {
		this.my_cash = my_cash;
	}
	
	
}

package entity;

import java.io.Serializable;
import java.util.Date;

public class Deal implements Serializable{

	Integer id;
	
	String title;
	String text;
    String dealAvatar;
    String carModel;
    String travelDistance;
    String buyDate;
    String price;
    String SellerName;
    
    Date createDate;
	Date editDate;
	
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDealAvatar() {
		return dealAvatar;
	}
	public void setDealAvatar(String dealAvatar) {
		this.dealAvatar = dealAvatar;
	}
	public String getCarModel() {
		return carModel;
	}
	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}
	public String getTravelDistance() {
		return travelDistance;
	}
	public void setTravelDistance(String travelDistance) {
		this.travelDistance = travelDistance;
	}
	public String getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSellerName() {
		return SellerName;
	}
	public void setSellerName(String sellerName) {
		SellerName = sellerName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getEditDate() {
		return editDate;
	}
	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}
	
}

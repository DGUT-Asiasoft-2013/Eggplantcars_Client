package entity;

import java.io.Serializable;
import java.util.Date;

public class ShoppingCar implements Serializable{
	
	
	public static class Key implements Serializable {
		Deal deal;
		User buyer;
		
		
		public Deal getDeal() {
			return deal;
		}
		public void setDeal(Deal deal) {
			this.deal = deal;
		}
		
		public User getBuyer() {
			return buyer;
		}
		public void setBuyer(User buyer) {
			this.buyer = buyer;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				Key other = (Key) obj;
				return deal.getId() == other.deal.getId() && buyer.getId() == other.buyer.getId();
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return deal.getId();
		}
	}
	
	Key id;
	Date createDate;
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}

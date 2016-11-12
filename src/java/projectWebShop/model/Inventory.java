/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustav
 */
@Entity
@Table(name = "INVENTORY", schema="APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Inventory.findAll", query = "SELECT i FROM Inventory i"),
    @NamedQuery(name = "Inventory.findByItemname", query = "SELECT i FROM Inventory i WHERE i.itemname = :itemname"),
    @NamedQuery(name = "Inventory.findByPrice", query = "SELECT i FROM Inventory i WHERE i.price = :price"),
    @NamedQuery(name = "Inventory.findByAmount", query = "SELECT i FROM Inventory i WHERE i.amount = :amount")})
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "ITEMNAME")
    private String itemname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRICE")
    private double price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AMOUNT")
    private int amount;

    public Inventory() {
    }

    public Inventory(String itemname) {
        this.itemname = itemname;
    }

    public Inventory(String itemname, double price, int amount) {
        this.itemname = itemname;
        this.price = price;
        this.amount = amount;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (itemname != null ? itemname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Inventory)) {
            return false;
        }
        Inventory other = (Inventory) object;
        if ((this.itemname == null && other.itemname != null) || (this.itemname != null && !this.itemname.equals(other.itemname))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectWebShop.model.Inventory[ itemname=" + itemname + " ]";
    }
    
}

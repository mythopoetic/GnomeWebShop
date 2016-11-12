/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.inject.Inject;
import projectWebShop.controller.ShopFacade;
import projectWebShop.model.Inventory;
import projectWebShop.model.Users;


@Named("shopManager")
@ConversationScoped

/**
 *The backing bean. All requests from the user interface goes through here.
 * 
 * @author Gustav
 */
public class ShopManager implements Serializable {

    @EJB
    private ShopFacade sf;
    @Inject
    private Conversation conversation;
    private String conversationID;
    private boolean blocked;
    private boolean loggedIn;
    private String username;
    private String password;
    private Exception transactionFailure;
    private List<Inventory> inventory;
    private int amountToCart;
    private int amountToBuy;
    private Inventory itemToCart;
    private List<Inventory> shoppingCart;
    private double totalSum;
    private List<Users> registered;
    private String addItemName;
    private double addPrice;
    private int addAmount;
    private Integer amountToRefill;
    private int copyToCart;
    private boolean isAdmin;
    private Users user;
    private boolean isRegistered;
    private String blockedUser;
    private boolean isBlocked;
    private boolean isUnblocked;
    private String refillItem;
    private boolean refilled;
    private String toCart;
    private String removeItem;
    private boolean added;
    private boolean removed;
    private Integer copyAmount;
    private Map<String, String> restockAmount = new HashMap<>();
    private Map<String, String> buyAmount = new HashMap<>();

    /**
     * Next two functions for sarting and stopping a conversation
     */
    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }
    
    
    /**
     * Next three methods handle exceptions
     */
    private void handleException(Exception e) {
        //stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e;
    }

    public boolean getSuccess() {
        return transactionFailure == null;
    }

    public Exception getException() {
        return transactionFailure;
    }

    /**
     * Called on log in. Checks if user is blocked and if user is admin
     */
    public void login() {
        try {
            startConversation();
            conversationID = conversation.getId();
            isAdmin = false;
            loggedIn = false;
            transactionFailure = null;
            user = sf.login(username, password);
            blocked = sf.checkBlock(username);
            if (blocked) {
                throw new Exception("User blocked by admin");
            } else if (user.getUsername().equals("admin")) {
                isAdmin = true;
            }
            loggedIn = true;   
        } catch (Exception e) {
            stopConversation();
            handleException(e);
        }
    }

    /**
     * Called when registering a new user
     */
    public void register() {
        try {
            transactionFailure = null;
            user = sf.register(username, password);
            isRegistered = true;
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    /**
     * Called on logout. Sets relevant booleans to false and username to an
     * empty string
     */
    public void logout() {
        transactionFailure = null;
        loggedIn = false;
        isAdmin = false;
        isRegistered = false;
        username = "";
        stopConversation();
    }

    /**
     * Calls the browse method in the controller which returns a list with all
     * items currently in the database
     */
    public void browse() {
        try {
            transactionFailure = null;
            inventory = sf.browse();
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Calls getRemaining to get the remaining amount in stock for the specific
     * item to check that the amount to put in cart does not exceed. After that
     * calls the method in the controller to put the specified item in the
     * shopping cart and returns that list.
     * 
     * @param item The item to put in the shopping cart
     */
    public void putInCart(Inventory item) {
        try {
            transactionFailure = null;
            toCart = item.getItemname();
            amountToCart = Integer.parseInt(buyAmount.get(toCart));
            if (amountToCart <= 0) {
                throw new Exception("You must add 1 or more");
            }
            int inStock = sf.getRemaining(item);
            if(toCart.equals("Christian")) {
                throw new Exception("You cannot buy Christian. He is unbuyable!");
            }
            if (amountToCart > inStock) {
                throw new Exception("You cannot buy that many, only " + inStock + " left in stock");
            } else {
                shoppingCart = sf.putInCart(item, amountToCart);
            }
            copyToCart = amountToCart;
            amountToCart = 0;
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Returns the total sum of all the items currently in the shopping cart
     */
    public void updateCart() {
        transactionFailure = null;
        totalSum = sf.getSum();
    }

    /**
     * Calls the buy method in the controller that returns the shopping cart
     * equal to null. Sets the total sum to 0 in order to make it not render on
     * the page (adminGUI.xhtml)
     */
    public void buy() {
        try {
            transactionFailure = null;
            shoppingCart = sf.buy();
            totalSum = 0.0;
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Calls the method in the controller to get a list of all registered users
     */
    public void showUsers() {
        try {
            transactionFailure = null;
            registered = sf.showUsers();
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Calls the method in the controller to block or unblock the specified
     * user.
     * @param user The user to be blocked or unblocked
     */
    public void blockOrUnblock(Users user) {
        blockedUser = user.getUsername();
        isBlocked = sf.blockOrUnblock(user.getUsername());
        isUnblocked = !isBlocked;
    }

    /**
     * Calls the method in the controller to restock the specified item with
     * the specified amount.
     * @param item The item to restock
     */
    public void refillStock(Inventory item) {
        try {
            transactionFailure = null;
            refilled = false;
            refillItem = item.getItemname();
            amountToRefill = Integer.parseInt(restockAmount.get(refillItem));
            if (amountToRefill <= 0) {
                throw new Exception("You must refill stock with at least one or more");
            }
            sf.refillStock(refillItem, amountToRefill);
            refilled = true;
            copyAmount = amountToRefill;
            amountToRefill = 0;
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Called when adding a new item to the database.
     */
    public void addNewItem() {
        try {
            added = false;
            transactionFailure = null;
            if (addPrice <= 0.0 || addAmount <= 0) {
                throw new Exception("Price and amount must be greater than zero");
            }
            boolean succ = sf.addNewItem(addItemName, addPrice, addAmount);
            if (!succ) {
                throw new Exception("The item must have a unique name");
            }
            added = true;
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Called when removing an item from the database
     */
    public void remove() {
        try {
            removed = false;
            transactionFailure = null;
            sf.remove(removeItem);
            removed = true;
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * All code below required getters and setters
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setAmountToCart(int amountToCart) {
        this.amountToCart = amountToCart;
    }

    public int getAmountToCart() {
        return amountToCart;
    }

    public void setAmountToBuy(int amountToBuy) {
        this.amountToBuy = amountToBuy;
    }

    public int getAmountToBuy() {
        return amountToBuy;
    }

    public Inventory getItemToCart() {
        return itemToCart;
    }

    public List<Inventory> getShoppingCart() {
        return shoppingCart;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public List<Users> getRegistered() {
        return registered;
    }

    public void setAddItemName(String addItemName) {
        this.addItemName = addItemName;
    }

    public String getAddItemName() {
        return addItemName;
    }

    public void setAddPrice(double addPrice) {
        this.addPrice = addPrice;
    }

    public double getAddPrice() {
        return addPrice;
    }

    public void setAddAmount(int addAmount) {
        this.addAmount = addAmount;
    }

    public int getAddAmount() {
        return addAmount;
    }

    public void setAmountToRefill(int amountToRefill) {
        this.amountToRefill = amountToRefill;
    }

    public int getAmountToRefill() {
        return amountToRefill;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public String getBlockedUser() {
        return blockedUser;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public boolean getIsUnblocked() {
        return isUnblocked;
    }

    public String getRefillItem() {
        return refillItem;
    }

    public boolean getRefilled() {
        return refilled;
    }

    public boolean getAdded() {
        return added;
    }

    public int getCopyAmount() {
        return copyAmount;
    }

    public Map<String, String> getRestockAmount() {
        return restockAmount;
    }

    public Map<String, String> getBuyAmount() {
        return buyAmount;
    }

    public int getCopyToCart() {
        return copyToCart;
    }

    public String getToCart() {
        return toCart;
    }

    public void setRemoveItem(String removeItem) {
        this.removeItem = removeItem;
    }

    public String getRemoveItem() {
        return removeItem;
    }

    public boolean getRemoved() {
        return removed;
    }
    
    public String getConversationID() {
        return conversationID;
    }

}

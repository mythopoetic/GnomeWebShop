/*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectWebShop.controller;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import projectWebShop.model.Inventory;
import projectWebShop.model.ShopBusinessLogic;
import projectWebShop.model.Users;

/**
 * The backing bean. All requests from the user interface goes through here.
 * 
 * @author Gustav
 * @author Teo
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateful

public class ShopFacade {

    @PersistenceContext(unitName = "projectWebShopPU")
    private EntityManager em;
    private List<Inventory> items;
    private List<Users> allUsers;
    private ArrayList<Inventory> inventoryList = new ArrayList<>();
    private ArrayList<Inventory> shoppingCart = new ArrayList<>();
    private ArrayList<Users> registered = new ArrayList<>();
    private double sum;
    private ShopBusinessLogic sbl = new ShopBusinessLogic();
    private Inventory buyItem;
    private Users user;
    private Inventory newItem;

    /**
     * Searches the Users table in the database database for a user
     * corresponing to the specified username on log in.
     * 
     * @param username  the username typed in by the user on log in.
     * @param password  the password typed in by the user on log in.
     * @return          the entity found in the database corresponding
     *                  to the username.
     * @throws Exception if no matching user could be found in the database
     *                   or the password doesn't match.
     */
    public Users login(String username, String password) throws Exception{
        user = em.find(Users.class, username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Wrong username or password");
        }
        return user;
    }

    /**
     * Checks if the user with the specified username is blocked or not by
     * getting the corresponding blocked-column from the Users table in the
     * database on log in.
     * 
     * @param username  the username typed in by the user on log in.
     * @return          the value from the corresponding blocked column, e.g.
     *                  true or false.
     */
    public boolean checkBlock(String username) {
        user = em.find(Users.class, username);
        return user.getBlocked();
    }

    /**
     * Registers a new user in the system by creating a new entity and saving
     * it in the Users table in the database.
     * 
     * @param username  the username typed in by the user when registering.
     * @param password  the password typed in by the user when registering
     * @return          the persisted entity, e.g. the newly registered user
     * @throws Exception if a user with the specified username already exists
     */
    public Users register(String username, String password) throws Exception{
        Users newUser = em.find(Users.class, username);
        if (newUser != null && newUser.getUsername().equals(username)) {
            throw new Exception("User already exists!");
        }
        user = new Users(username, password, false);
        em.persist(user);
        return user;
    }

    /**
     * Collects all rows from the Inventory table in the databse and inserts
     * them into a list.
     * 
     * @return  the list containing all rows collected from the database.
     */
    public List<Inventory> browse() {
        items = em.createNamedQuery("Inventory.findAll", Inventory.class).getResultList();
        if(items == null) {
            throw new EntityNotFoundException("No items currently in the database");
        }
        for (Inventory item : items) {
            if (!inventoryList.contains(item)) {
                inventoryList.add(item);
            }
        }
        return inventoryList;
    }

    /**
     * Creates a new item to put in the shopping cart list and returns the list.
     * Also calls the calcSum method to get the total sum
     */
    
    /**
     * Creates a new item to put in the shopping cart. Adds the new item to
     * the shopping cart list. Also calls the 
     * {@link ShopBusinessLogic#calcSum(int, double, double) calc Sum} method
     * to calculate the total sum in the shopping cart.
     * 
     * @param item          the item to put in the shopping cart
     * @param amountToCart  the amount of the specified item to put in the
     *                      shopping cart
     * @return              the list containing the items in the shopping cart
     */
    public List<Inventory> putInCart(Inventory item, int amountToCart) {
        Inventory itemToCart = new Inventory(item.getItemname(), item.getPrice(), amountToCart);
        shoppingCart.add(itemToCart);
        sum = sbl.calcSum(itemToCart.getAmount(), itemToCart.getPrice(), sum);
        return shoppingCart;
    }

    /**
     * Gets the sum of all the items in the shopping cart
     * 
     * @return the sum of the items in the shopping cart
     */
    public double getSum() {
        return sum;
    }

    /**
     * Finds the item in the Inventory table in the database corresponding to
     * the specified item name to get the amount left in stock.
     * 
     * @param item  the item to put in the shopping cart.
     * @return      the amount of the specified item left in the database
     */
    public int getRemaining(Inventory item) {
        Inventory entity = em.find(Inventory.class, item.getItemname());
        return entity.getAmount();
    }

    /**
     * Loops through the list containing the items in the shopping cart and gets
     * each corresponding item from the Inventory table in the database. Removes
     * the item from the database if the amount to buy equals the amount left
     * in stock, otherwise decreases the amount left in stock with the amount to
     * buy. Resets the sum in the shopping cart to zero and the shopping cart
     * list to null.
     * 
     * @return           the shopping cart list reset to null
     * @throws Exception if the shopping cart list is empty
     */
    public List<Inventory> buy() throws Exception{
        if(shoppingCart == null) {
            throw new Exception("No items in shopping cart");
        }
        for (Inventory item : shoppingCart) {
            buyItem = em.find(Inventory.class, item.getItemname());
            int amount = buyItem.getAmount();
            if (item.getAmount() == amount) {
                em.remove(buyItem);
            } else {
                buyItem.setAmount(amount - item.getAmount());
            }
        }
        sum = 0.0;
        return shoppingCart = null;
    }

    /**
     * Collects all rows from the Users table in the database in a list.
     * For each collected user in the list, if not contained in the list over
     * registered users adds it to that list.
     * 
     * @return the list containing all registered users.
     */
    public List<Users> showUsers() {
        allUsers = em.createNamedQuery("Users.findAll", Users.class).getResultList();
        if(allUsers == null) {
            throw new EntityNotFoundException("No registered users in the database");
        }
        for (Users user : allUsers) {
            if (!registered.contains(user)) {
                registered.add(user);
            }
        }
        return registered;
    }

    /**
     * Finds the specified user in the database and sets the value of the 
     * blocked-variable for that user to the opposite of the current value, 
     * either true or false.
     * 
     * @param name the name of the user to be blocked or unblocked
     * @return     true if user got blocked, false if unblocked
     */
    public boolean blockOrUnblock(String name) {
        Users blockName = em.find(Users.class, name);
        if (!blockName.getBlocked()) {
            blockName.setBlocked(true);
            return true;
        } else {
            blockName.setBlocked(false);
            return false;
        }
    }

    /**
     * Finds the specified item in the database and gets the current amount left
     * in stock. The method {@link ShopBusinessLogic#calcAmount(int, int) calcAmount}
     * is then called to calculate the new amount and it is updated in the database.
     * 
     * @param itemName       the name of the item which is to be re-stocked
     * @param amountToRefill the amount to add to the current stock
     */
    public void refillStock(String itemName, int amountToRefill) {
        Inventory item = em.find(Inventory.class, itemName);
        int currentAmount = item.getAmount();
        int currentStock = sbl.calcAmount(currentAmount, amountToRefill);
        item.setAmount(currentStock);
    }

    /**
     * Creates a new item to be added to the Inventory table in the database.
     * Adds the item to the table if another item with the same item name is
     * not already present in the database.
     * 
     * @param itemName  the name of the new item to be added to the database
     * @param price     the price of the item
     * @param amount    the amount to be put in stock
     * @return          true if the item was added, false if the item is already
     *                  present in the database
     */
    public boolean addNewItem(String itemName, double price, int amount) {
        newItem = new Inventory(itemName, price, amount);
        if (em.find(Inventory.class, itemName) == null) {
            em.persist(newItem);
        }else {
            return false;
        }
        return true;
    }
    
    /**
     * Finds the specified item in the database and removes it. Also removes it
     * from the list containing all items in stock.
     * 
     * @param item the name of the item to be removed
     */
    public void remove(String item) {
        Inventory removeItem = em.find(Inventory.class, item);
        inventoryList.remove(removeItem);
        em.remove(removeItem);
    }

}

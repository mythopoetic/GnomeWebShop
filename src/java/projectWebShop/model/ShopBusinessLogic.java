
package projectWebShop.model;

/**
 *
 * @author Gustav
 */
public class ShopBusinessLogic {
    
    public ShopBusinessLogic() {
    }
    
    /**
     * Calculates the sum of all items in the shopping cart.
     * 
     * @param amount the amount of the item added to the shopping cart.
     * @param price  the price for the corresponding item.
     * @param sum    the sum of previous items in the shopping cart.
     * @return       the total price of all items in the cart.
     */
    public double calcSum(int amount, double price, double sum) {
        double total = sum + amount*price;
        return total;
    }
    
    /**
     * Calculates the new amount when re-stocking.
     * 
     * @param refill  the amount to add to the current stock
     * @param current the current amount in stock for the item
     * @return        the new amount in stock
     */
    public int calcAmount(int refill, int current) {
        int stock = refill + current;
        return stock;
    }
}

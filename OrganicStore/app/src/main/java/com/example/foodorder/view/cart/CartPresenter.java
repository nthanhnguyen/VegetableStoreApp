package com.example.foodorder.view.cart;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.foodorder.ControllerApplication;
import com.example.foodorder.R;
import com.example.foodorder.constant.Constant;
import com.example.foodorder.database.FoodDatabase;
import com.example.foodorder.model.Food;
import com.example.foodorder.model.Order;
import com.example.foodorder.utils.StringUtil;
import com.example.foodorder.utils.Utils;

import java.util.List;

public class CartPresenter {

    private final CartMVPView mCartMVPView;

    public CartPresenter(CartMVPView mCartMVPView) {
        this.mCartMVPView = mCartMVPView;
    }

    public void getListFoodInCart(Context context) {
        List<Food> list = FoodDatabase.getInstance(context).foodDAO().getListFoodCart();
        mCartMVPView.loadListFoodInCart(list);
    }

    public void calculateTotalPrice(Context context) {
        List<Food> listFoodCart = FoodDatabase.getInstance(context).foodDAO().getListFoodCart();
        if (listFoodCart == null || listFoodCart.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            mCartMVPView.loadCalculatePriceResult(strZero, 0);
            return;
        }

        int totalPrice = 0;
        for (Food food : listFoodCart) {
            totalPrice = totalPrice + food.getTotalPrice();
        }

        String strTotalPrice = totalPrice + Constant.CURRENCY;
        mCartMVPView.loadCalculatePriceResult(strTotalPrice, totalPrice);
    }

    public void deleteFoodFromCart(Context context, Food food, int position) {
        FoodDatabase.getInstance(context).foodDAO().deleteFood(food);
        mCartMVPView.deleteFoodFromCartSuccess(position);
    }

    public void updateFoodInCart(Context context, Food food, int position) {
        FoodDatabase.getInstance(context).foodDAO().updateFood(food);
        mCartMVPView.updateFoodInCartSuccess(position);
    }

    public String getStringListFoodsOrder(Context context, List<Food> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String result = "";
        for (Food food : list) {
            if (StringUtil.isEmpty(result)) {
                result = "- " + food.getName() + " (" + food.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + context.getString(R.string.quantity) + " " + food.getCount();
            } else {
                result = result + "\n" + "- " + food.getName() + " (" + food.getRealPrice()
                        + Constant.CURRENCY + ") "
                        + "- " + context.getString(R.string.quantity) + " " + food.getCount();
            }
        }
        return result;
    }

    public void sendOrderToFirebase(Context context, long id, @NonNull Order order) {
        ControllerApplication.get(context).getBookingDatabaseReference()
                .child(Utils.getDeviceId(context))
                .child(String.valueOf(id))
                .setValue(order, (error1, ref1) -> mCartMVPView.sendOderSuccess());
    }

    public void deleteAllFoodInCart(Context context) {
        FoodDatabase.getInstance(context).foodDAO().deleteAllFood();
        mCartMVPView.deleteAllFoodInCartSuccess();
    }
}

package com.cloudteam.handler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cloudteam.hackathonServer.newServer;
import com.cloudteam.utils.DButils;
import com.cloudteam.utils.RedisOperator;
import com.cloudteam.utils.TokenGenerator;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Food {

	public String return_info = null;
	public Food() {
		// TODO Auto-generated constructor stub
	}

	public int AddFoodHand(String food_info, String access_token, String cart_id){
		RedisOperator operator = new RedisOperator();
		String token = access_token;
		JSONObject food = new JSONObject();
		try{
		food = JSONObject.fromObject(food_info);//不可能为空，在外面判断过了
		} catch(JSONException e ){
			e.printStackTrace();
			return newServer.ErrorCode_MALFORMED_JSON;
		}
		if(cart_id.equals(null) || cart_id.equals(""))
			return newServer.ErrorCode_CART_NOT_FOUND;  //篮子不存在
		if(Integer.valueOf(food.get("food_id").toString()) > 100 || Integer.valueOf(food.get("food_id").toString()) < 1 ){
			return newServer.ErrorCode_FOOD_NOT_FOUND;
		}
		
		//判断篮子是否存在
		if(operator.isCartsExist(cart_id))  //存在
		{
			if(TokenGenerator.getInstance().User2Token.get(cart_id).equals(token))  //篮子是他的
			{
				boolean isOver=operator.isOverLimits(cart_id,
						food.get("food_id").toString(), 
						Integer.parseInt(food.get("count").toString()));							
				if(isOver){
					return newServer.ErrorCode_FOOD_OUT_OF_LIMIT;
				}else{
					return newServer.SuccessCode;
				}

			}else
			{
				return newServer.ErrorCode_NOT_AUTHORIZED_TO_ACCESS_CART;
			}
		}else
		{
			return newServer.ErrorCode_CART_NOT_FOUND;
		}
		
	}
}

import React, {useState, useEffect} from 'react';
import { Boardgame } from './boardgames';


export interface OrderedBoardgameId {
  orderId: number,
  boardgameName: string,

}

export interface OrderedBoardgame {
  orderedBoardgameId: OrderedBoardgameId
  order: Order,
  boardgame: Boardgame,
  quantity: number

}

export interface User {
    id: string,
    email: string,
    name: string
}

export interface Order {
    id: number,
    user: User,
    date: string
}

export class SearchEveryOrder {

  public async handleSearch(): Promise<OrderedBoardgame[]>{

    let endpoint = 'http://localhost:8080/api/is_admin';

    let isAdminResponse = await fetch(
      endpoint,
      {
        method: 'GET',
        credentials: 'include',
        headers: {'Content-type':'application/json'}
      }
    );

    let jsonData = await isAdminResponse.json();
    if ( jsonData == true ){
      endpoint = 'http://localhost:8080/api/orders/admin';
    }else {
      endpoint='http://localhost:8080/api/orders/';
    }

    //Fetch results from backend
    const response = await fetch(
      endpoint,
      {
        method: 'GET',
        credentials: 'include',
        headers: {'Content-Type': 'application/json'}
      });
    jsonData = await response.json();

    const orders : OrderedBoardgame[] = jsonData;
    console.log(orders);
    
    // Frontend-side filter, it's useless since it is managed by the backend
    //const filteredProducts = boardgames.filter((bg) => bg.name.toLowerCase().includes(q) );
    
    return orders;
  }

};

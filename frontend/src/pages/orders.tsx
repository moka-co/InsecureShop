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

    //probably there is a better way to do this:
    const endpoint = 'http://localhost:8080/api/orders/admin';
    console.log(endpoint);

    //Fetch results from backend
    const response = await fetch(
      endpoint,
      {
        method: 'GET',
        credentials: 'include',
        headers: {'Content-Type': 'application/json'}
      });
    const jsonData = await response.json();

    const orders : OrderedBoardgame[] = jsonData;
    console.log(orders);
    
    // Frontend-side filter, it's useless since it is managed by the backend
    //const filteredProducts = boardgames.filter((bg) => bg.name.toLowerCase().includes(q) );
    
    return orders;
  }

};

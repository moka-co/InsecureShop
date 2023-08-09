import React, {useState, useEffect} from 'react';

export interface Boardgame {
  name: String,
  price: number,
  quantity: number,
  description: String
}

export class SearchBoardgames {

  public async handleSearch(q: string): Promise<Boardgame[]>{

    const endpoint = `http://localhost:8080/api/boardgames?q=${q}`;
    console.log(endpoint);

    //Fetch results from backend
    const response = await fetch(endpoint);
    const jsonData = await response.json();

    const boardgames : Boardgame[] = jsonData;
    
    // Frontend-side filter, it's useless since it is managed by the backend
    //const filteredProducts = boardgames.filter((bg) => bg.name.toLowerCase().includes(q) );
    
    return boardgames;
  }

};

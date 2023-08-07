import React, { useState, useEffect } from 'react';
import {SearchEveryOrder, OrderedBoardgame, Order } from './orders';
import {Boardgame} from './boardgames';
import { group } from 'console';

const EveryOrder: React.FC = () => {
    const searchEveryOrderInstance = new SearchEveryOrder();
    const [searchResults, setSearchResults] = useState<OrderedBoardgame[]>([]);
    const printedUserIds = new Set<number>();
    let selectedOrders = new Array<number>();

    useEffect(()=>{
        //List of every orders
        const results = searchEveryOrderInstance.handleSearch();
        results.then((result) => setSearchResults(result));

    }, []);

    const handleSelectOrdersButton = (orderId: number) => {
      if (selectedOrders.includes(orderId)){

        selectedOrders = selectedOrders.filter((number) => number != orderId);
      }else {
        selectedOrders.push(orderId);
      }
    }

    const handleDeleteOrderButton = async () => {
      for(let i=0; i<selectedOrders.length; i++){
        let orderId = selectedOrders[i];
        let uri = `http://localhost:8080/api/orders/${orderId}/delete`;

        let response = await fetch(
          uri,
          {
            method: 'GET', 
            credentials: 'include',
            headers: {'Content-type': 'appication/json'}
          }
        );
      };
      window.location.reload();

    }


      // Group the orders by their IDs using the reduce method
  const groupedOrders = searchResults.reduce((map, orderb) => {
    
    const orderId = orderb.order.id;
    if (!map.has(orderId)) {
      map.set(orderId, []);
    }
    map.get(orderId)?.push([orderb.order, orderb.boardgame, orderb.quantity]);
    return map;
  }, new Map<number, [Order, Boardgame, number][]>());

    return (
    <div>

      {/* Render the search results */}
      <div>
        {searchResults.length > 0 && Array.from(groupedOrders).map(([orderId, orders], index) => (
          <div key={index}>
            <h4 className="text-xl font-bold">Order ID: {orderId}</h4>
            <button onClick={handleDeleteOrderButton}>elimina</button>
            <br></br>

            {orders.map((order, subIndex) => (
              <div key={subIndex} className="border p-4">
                {printedUserIds.has(orderId) == false && (
                    <>
                    <input type="checkbox" onClick={(event)=> handleSelectOrdersButton(orderId)}></input>
                    <p className="text-gray-600"> Username: {order[0].user.name}</p>
                    <p> Email: {order[0].user.id} </p>
                    <span className="text-green-600 font-semibold"> Date: {order[0].date}</span>
                    {printedUserIds.add(orderId)}
                    </>
                )}
                <p>Game: {order[1].name}</p>
                <p>Quantity: {order[2]}</p>
                <p>Price: {order[1].price}</p>
              </div>
            ))}
          </div>
        ))}
        
      </div>
    </div>
  );

}

export default EveryOrder;
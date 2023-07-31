import React, {useState, useEffect} from 'react';

interface Boardgame {
  name: String,
  price: Float32Array,
  quantity: number,
  description: String
}

const endpoint ='http://localhost:8080/api/boardgames';

const Boardgames: React.FC = () => {
  const [boardgames, setBoardgames] = useState<Boardgame[]>([]);

  useEffect(() => {
    async function fetchData() {
      const response = await fetch(endpoint);
      const jsonData = await response.json();
      console.log(jsonData);
      setBoardgames(jsonData);

    }

    fetchData();
    }, []);

  return (
    <div className="grid grid-cols-3 gap-4">
      {
        boardgames.map((boardgame, index) => (
          <div key={index} className="border p-4">
            <h2 className="text-xl font-bold">{boardgame.name}</h2>
            <p className="text-gray-600"> {boardgame.description}</p>
            <span className="text-green-600 font-semibold"> {boardgame.price}</span>

          </div>
        ))
      }

    </div>
  )


};

export default Boardgames;
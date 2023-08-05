import React, { useState, useEffect } from 'react';
import { Boardgame, SearchBoardgames } from './boardgames';


const ManageBoardgames: React.FC = () => {
    const searchBoardgamesInstance = new SearchBoardgames();
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearchClicked, setIsSearchClicked] = useState(false);
    const [searchResults, setSearchResults] = useState<Boardgame[]>([]);
    let selectedBoardgame: string = "";

    useEffect(()=>{
        //List of every boardgames
        const results = searchBoardgamesInstance.handleSearch(searchTerm);
        results.then((result) => setSearchResults(result));

    }, []);

    //this method get called when you change something in the input form
    //Change search term inside the search bar and set search clicked to 0
    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
        setIsSearchClicked(false);
    };

    //This method get called when the user click on the "Search" button
    //it updates searchResults variable with results from API call
    const handleSearchClick = async () => {
        const results = await searchBoardgamesInstance.handleSearch(searchTerm);
        setSearchResults(results);
        setIsSearchClicked(true);
    };

    const handleDeleteBoardgameButton = async () => {
        let uri = `http://localhost:8080/api/boardgames/${selectedBoardgame}/delete`;

        console.log(uri);
        let response = await fetch(
          uri,
          {
            method: 'GET', 
            credentials: 'include',
            headers: {'Content-type': 'appication/json'}
          }
        );
        
      window.location.reload();

    }

    const handleSelectOrdersButton = (boardgameName: String, index: number) => {
        selectedBoardgame=boardgameName.toString();

        let searchResultsElement = document.querySelector(".searchResultsDiv");  //document.getElementsByClassName("searchResultsDiv");

        if ( searchResultsElement != null) {
            const childrenElements = searchResultsElement.querySelectorAll('div');
            childrenElements.forEach((searchResult) => {
                let keyString = searchResult.getAttribute('id');
                let key = -1;
                if ( keyString != null){
                    key = parseInt(keyString);
                }
                const checkboxInput = searchResult.querySelector('input[type="checkbox"]') as HTMLInputElement;
                if ( key != index &&  checkboxInput){
                    checkboxInput.checked = false;
                }

            });
        }

      }

    return (
        <div>
            <input
                type="text"
                value={searchTerm}
                onChange={handleInputChange}
                placeholder="Search..."
            />
            <button onClick={handleSearchClick}>Search</button>
            <div>
                <button onClick={handleDeleteBoardgameButton}>Elimina</button>
            </div>
            <div>
                {/* Here, I found out: React escapes characters */}
                { isSearchClicked && searchTerm != '' && 
                    <p dangerouslySetInnerHTML={{"__html": "You searched for: " + searchTerm}}/>
                } 
                <div className="searchResultsDiv">
                {searchResults.length > 0 && searchResults.map((boardgame, index) => (
                    
                        <div id={index.toString()} key={index} className="border p-4">
                        <h2 className="text-xl font-bold">{boardgame.name}</h2>
                        <p className="text-gray-600"> {boardgame.description}</p>
                        <p>{boardgame.quantity}</p>
                        <span className="text-green-600 font-semibold">Prezzo: {boardgame.price} €</span>
                        <br></br>
                        <input type="checkbox" onChange={(event)=> handleSelectOrdersButton(boardgame.name, index)}></input>
                        </div>
                    

                ))}
                </div>
            </div>

        </div>
    );

}

export default ManageBoardgames
import React, { useState, useEffect } from 'react';
import { Boardgame, SearchBoardgames } from './boardgames';


const ManageBoardgames: React.FC = () => {
    const searchBoardgamesInstance = new SearchBoardgames();
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearchClicked, setIsSearchClicked] = useState(false);
    const [searchResults, setSearchResults] = useState<Boardgame[]>([]);
    let selectedBoardgame: string = "";
    const [isModifyButtonClicked, setIsModifiedButtonClicked] = useState(false);
    let selectedBoardgameObject: Boardgame = {'name':"", 'price': 0 , 'quantity':0,'description':"" };
    const [selectedBoardgameComponent, setSelectedBoardgameComponent] = useState<JSX.Element | null>(null);
    const [isAddButtonClicked, setIsAddButtonClicked] = useState(false);

    let newBoardgameObject: Boardgame = {'name':"", 'price': 0 , 'quantity':0,'description':"" };
    const [newBoardgameComponent, setNewBoardgameComponent] = useState<JSX.Element | null>(null);   

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

    const handleSelectOrdersButton = async (boardgameName: String, index: number) => {
        selectedBoardgame=boardgameName.toString(); //Save string name, may delete later
        setIsModifiedButtonClicked(false);

        let uri = `http://localhost:8080/api/boardgames/${boardgameName}`;
        const response = await fetch(uri,
            {
                'method': 'GET',
                'credentials': 'include',
                headers: {'Content-type': 'appication/json'}
            }
        );
        let jsonData = await response.json();
        selectedBoardgameObject = jsonData;
        selectedBoardgameObject.name = boardgameName;


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

      const handleSubmitBoardgameEditsButton = () => {

        let price = selectedBoardgameObject.price;
        let description = selectedBoardgameObject.description;
        let quantity = selectedBoardgameObject.quantity;
        let uri = `http://localhost:8080/api/boardgames/${selectedBoardgame}/edit?price=${price}&quantity=${quantity}&description=`;
        uri = uri + encodeURIComponent(description.toString());
        fetch(
            uri,
            {
                'method': 'GET',
                'credentials': 'include',
                headers: {'Content-type' : 'application/json'}
            }
        ).then((response)=> window.location.reload());
      }

      const handleSubmitBoardgameAddButton = () => {
        let name = newBoardgameObject.name;
        let price = newBoardgameObject.price;
        let description = newBoardgameObject.description;
        let quantity = newBoardgameObject.quantity;
        let uri = `http://localhost:8080/api/boardgames/add?name=`;
        uri = uri + encodeURIComponent(name.toString()) + `&price=${price}&quantity=${quantity}&description=`;
        uri = uri + encodeURIComponent(description.toString());

        fetch(
            uri,
            {
                'method': 'GET',
                'credentials': 'include',
                headers: {'Content-type' : 'application/json'}
            }
        ).then( (response) => window.location.reload());

      }

      async function getModifyBoardgameComponent(boardgameName: string) {

        const uri = `http://localhost:8080/api/boardgames/${boardgameName}`;
    
        const element = await fetch(uri,
            {
                'method': 'GET',
                'credentials': 'include',
                headers: {'Content-type': 'appication/json'}
            }
        ).then((response => {
            return response.json();
        })).then( (data) => {
            selectedBoardgameObject = data;
            return (
                <div> 
                <p>Name: <input type="text" defaultValue={boardgameName.toString()}></input></p>
                <p>Quantity: <input type="text" defaultValue={data.quantity} onChange={(event)=>selectedBoardgameObject.quantity= parseInt(event.target.value)}></input></p>
                <p>Description: <input type="text" defaultValue={data.description} onChange={(event)=>selectedBoardgameObject.description= event.target.value}></input></p>
                <p>Price: <input type="text" defaultValue={data.price} onChange={(event)=>selectedBoardgameObject.price= parseFloat(event.target.value)}></input></p>
                <button onClick={(event)=>handleSubmitBoardgameEditsButton()}>Conferma modifica</button>
                </div>
            );
        } )
    
        setSelectedBoardgameComponent(element);
    }

    function getAddBoardgameComponent() {
        const element = 
        (
            <div>
                <p>Name: <input type="text" defaultValue="" onChange={(event)=>newBoardgameObject.name= event.target.value}></input></p>
                <p>Quantity: <input type="text" defaultValue="0" onChange={(event)=>newBoardgameObject.quantity= parseInt(event.target.value)}></input></p>
                <p>Description: <input type="text" defaultValue="" onChange={(event)=>newBoardgameObject.description= event.target.value}></input></p>
                <p>Price: <input type="text" defaultValue="0" onChange={(event)=>newBoardgameObject.price= parseFloat(event.target.value)}></input></p>
                <button onClick={(event)=>handleSubmitBoardgameAddButton()}>Conferma modifica</button>
            </div>
        )

        setNewBoardgameComponent(element);

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
                <button onClick={(event)=> {
                    isModifyButtonClicked == true ? setIsModifiedButtonClicked(false) : setIsModifiedButtonClicked(true);
                    setIsAddButtonClicked(false);
                    getModifyBoardgameComponent(selectedBoardgame);
                }}>Modifica</button>
                <button onClick={(event)=> {
                    isAddButtonClicked == true ? setIsAddButtonClicked(false) : setIsAddButtonClicked(true);
                    setIsModifiedButtonClicked(false);
                    getAddBoardgameComponent();

                }} >Aggiungi</button>
                <>
                {isModifyButtonClicked == true && selectedBoardgameComponent}
                {isAddButtonClicked == true && newBoardgameComponent}
                </>

            </div>
            <div>
                {/* Here, I found out: React escapes characters */}
                { isSearchClicked && searchTerm != '' && 
                    <p dangerouslySetInnerHTML={{"__html": "You searched for: " + searchTerm}}/>
                } 
                <div className="searchResultsDiv">
                {searchResults.length > 0 && searchResults.map((boardgame, index) => (
                    
                        <div id={index.toString()} key={index} className="border p-4">
                        <h2 className="text-xl font-bold"><input type="checkbox" onChange={(event)=> handleSelectOrdersButton(boardgame.name, index)}></input> {boardgame.name} </h2>
                        <p className="text-gray-600"> {boardgame.description}</p>
                        <p>{boardgame.quantity}</p>
                        <span className="text-green-600 font-semibold">Prezzo: {boardgame.price} €</span>
                        <br></br>
                        
                        </div>
                ))}
                </div>
            </div>

        </div>
    );

}

export default ManageBoardgames;
import React, { useState, useEffect } from 'react';
import { SearchBoardgames, Boardgame } from './boardgames'
import { format } from 'date-fns';

async function CheckLogin() {
    const uri = 'http://localhost:8080/api/check_login';
  
    const response = await fetch(uri, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });

    const data = await response.json();

    const boolean : boolean = await data;

    return boolean;
};

async function setLoginComponentBuilder() {
    const boolean = await CheckLogin();

    if ( boolean == false){
        return (<div>
            <a href="http://localhost:8080/login">Log in</a>
        </div>
        ) ;
    }else {
        return (
        <div>
            <a href="http://localhost:8080/perform_logout">Log out</a>
        </div>
        
        );
    }

}

async function getMenuBarComponent() {
    const isLogged = await CheckLogin();
    if (isLogged == false) {
        return null;
    }

    const uri = 'http://localhost:8080/api/check_login';
  
    const response = await fetch(uri, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });

    const data = await response.json();

    const boolean : boolean = await data;

    if ( boolean == false ){ // isn't Admin
        return (
            <div>
                <a href="http://localhost:3000/my-orders">I miei ordini</a>
            </div>
        );
    }else { // is Admin
        return (
            <div>
                <br></br>
                <a href="http://localhost:3000/all-orders">Gestisci gli ordini</a>
                <br></br>
                <a href="http://localhost:3000/manage-boardgames">Gestisci boardgames</a>
            </div>
        );
    }

}

const HomePage: React.FC = () => {
    const searchBoardgamesInstance = new SearchBoardgames();
    const [savedToCart, setSavedToCart] = useState<{ [key: string] : number }>({});
    const [searchResults, setSearchResults] = useState<Boardgame[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearchClicked, setIsSearchClicked] = useState(false);
    const [loginComponent, setLoginComponent] = useState<JSX.Element | null>(null);
    const [menuBarComponent, setMenuBarComponent] = useState<JSX.Element | null>(null);
    const [loggedIn, setLoggedIn] = useState<boolean>(false);
    const [showCart, setShowCart] = useState<boolean>(false);

    useEffect(()=>{
        //Login / Logout
        setLoginComponentBuilder().then((element) => {
            setLoginComponent(element);
            setLoggedIn(true);
        });

        // Menu Bar
        getMenuBarComponent().then((element) => {
            setMenuBarComponent(element);
        });

        //List of every boardgames
        const results = searchBoardgamesInstance.handleSearch(searchTerm);
        results.then((result) => setSearchResults(result));

    }, []);

    //This method get called when the user click on the "Search" button
    //it updates searchResults variable with results from API call
    const handleSearchClick = async () => {
        const results = await searchBoardgamesInstance.handleSearch(searchTerm);
        setSearchResults(results);
        setIsSearchClicked(true);
    };

    //this method get called when you change something in the input form
    //Change search term inside the search bar and set search clicked to 0
    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
        setIsSearchClicked(false);
    };

    //Add clicked element to cart
    const handleAddToCartClick = async (event: React.MouseEvent<HTMLButtonElement>) => {
        const button = event.target as HTMLButtonElement;
        const div = button.parentNode as HTMLDivElement;
        const h2Element = div.querySelector('h2');
        const name = h2Element?.textContent;
        console.log('name:' + name);
        if ( typeof name == 'string'){
            updateCart(name);
        }

    }

    //Take a key and an increment, add +1 to an existing element inside object that represent cart
    const updateCart = (key: string, increment: number = 1) => {
        setSavedToCart((element) => (
            {
                ...element, [key]: (element[key] || 0) + increment,
            }
        ));
    };

    //Click to show or hide cart button
    const handleShowCartButton = () => {
        if ( showCart == false){
            setShowCart(true);
        }else{
            setShowCart(false);
        }

    }

    //Onclick of a button, remove a boardgame from button, uses updateCart function
    //Also checks if the savedToCart[key] is zero, then delete this from the object
    const handleRemoveBoardgameFromCartButton = (event: React.MouseEvent<HTMLButtonElement>, key: string) => {
        updateCart(key,-1);

        setSavedToCart((prevCart) =>  { //delete savedtoCart[key] from the array of objects if it is zero
            const updatedCart = Object.keys(savedToCart).filter((cartKey) => cartKey != key || prevCart[key] !== 0);
            const newCart :{ [key: string] : number } = { };
            updatedCart.forEach((cartKey) => {
                newCart[cartKey] = prevCart[cartKey];
            });
            return newCart;
        });
    }

    const makeOrder = async (event: React.MouseEvent<HTMLButtonElement>) => {
        const currentDate = format(new Date(), 'dd-MM-yyyy');

        let uri = `http://localhost:8080/api/orders/add?date=${currentDate}`;

        //Fetch results from backend
        const response = await fetch(
        uri,
        {
          method: 'GET',
          credentials: 'include',
          headers: {'Content-Type': 'application/json'}
        });

        const jsonData = await response.json()

        uri = "http://localhost:8080/api/orders/" +  encodeURIComponent(jsonData.id) + "/addBoardgame?";
        Object.keys(savedToCart).forEach(async (key) => {
            let newUri = `http://localhost:8080/api/orders/${jsonData.id}/addBoardgame?boardgameName=${key}&quantity=${savedToCart[key]}`;
            const response = await fetch(
                newUri,
                {
                  method: 'GET',
                  credentials: 'include',
                  headers: {'Content-Type': 'application/json'}
                });

        });
        window.location.reload();

    }

    return (
            <div>
                <h1>Welcome to Insecure Shop!</h1>
                <div>
                        {loginComponent}   
                        {menuBarComponent}
                </div>
                <br></br>
                <div>
                    { loggedIn && 
                        <button onClick={handleShowCartButton}>Mostra carrello <span id="cartLength" >({Object.keys(savedToCart).length})</span></button>
                    }

                    {
                        showCart && Object.keys(savedToCart).length > 0 && (
                            <div>
                                <h2>Carrello</h2>
                                <ul>
                                { Object.keys(savedToCart).map((key) => ( 
                                    savedToCart[key] > 0 && (
                                    <li key={key}>
                                        {key}: {savedToCart[key]} <button onClick={(event) => handleRemoveBoardgameFromCartButton(event, key)}>-</button>
                                    </li> 
                                    )
                                ))  }
                                </ul>
                            <button onClick={(event) => makeOrder(event)} >Compra</button>
                            </div>
                        )
                    }

                    
                    <br></br>
                </div>

                <div>
                    <input
                    type="text"
                    value={searchTerm}
                    onChange={handleInputChange}
                    placeholder="Search..."
                    />
                    <button onClick={handleSearchClick}>Search</button>

                    <div>
                        {/* Here, I found out: React escapes characters */}
                        { isSearchClicked && searchTerm != '' && 
                            <p dangerouslySetInnerHTML={{"__html": "You searched for: " + searchTerm}}/>
                        } 

                        {searchResults.length > 0 && searchResults.map((boardgame, index) => (
                            <div key={index} className="border p-4">
                                <h2 className="text-xl font-bold">{boardgame.name}</h2>
                                <p dangerouslySetInnerHTML={{"__html": "" + boardgame.description}}/>
                                <span className="text-green-600 font-semibold">Prezzo: {boardgame.price} €</span>
                                <br></br>
                                <button onClick={handleAddToCartClick}>add to chart</button>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
    );
};

export default HomePage;

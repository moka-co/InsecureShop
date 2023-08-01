import React, { useState } from 'react';
import { SearchBoardgames, Boardgame } from './boardgames'

const HomePage: React.FC = () => {
    const searchBoardgamesInstance = new SearchBoardgames();
    const [searchResults, setSearchResults] = useState<Boardgame[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearchClicked, setIsSearchClicked] = useState(false);
    var amILoggedIn: boolean = checkAmILoggedIn();

    //This method get called when the user click on the "Search" button
    //it updates searchResults variable with results from API call
    const handleSearchClick = async () => {
        const results = await searchBoardgamesInstance.handleSearch(searchTerm);
        setSearchResults(results);
        setIsSearchClicked(true);
    };

    function checkAmILoggedIn(): boolean {
        const uri = 'http://localhost:8080/api/check_login'
        const respon2 = fetch('http://localhost:8080/api/username');
        console.log(respon2);
        console.log("=================================");
        fetch(uri, {method: 'GET'}
            ).then((response) => {
                console.log(response);
                if ( response.ok){
                    return response.json();
                }
            }).then((data)=>{
                console.log("Returns: " + data);
                return data;
            }).catch((error) => {
                return false;
            })


        return false;
    }

    //this method get called when you change something in the input form
    //Change search term inside the search bar and set search clicked to 0
    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
        setIsSearchClicked(false);
    };

    return (
            <div>
                <h1>Welcome to Insecure Shop!</h1>
                <div>
                    <p> 
                        {amILoggedIn==false  &&<a href="http://localhost:8080/login">Log in</a>}
                        {amILoggedIn==true  && <p>You are logged in</p>}
                    </p>
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
                        <p>You searched for 
                            <p dangerouslySetInnerHTML={{"__html": searchTerm}}/>
                        </p>
                        } 

                        {searchResults.length > 0 && searchResults.map((boardgame, index) => (
                            <div key={index} className="border p-4">
                                <h2 className="text-xl font-bold">{boardgame.name}</h2>
                                <p className="text-gray-600"> {boardgame.description}</p>
                                <span className="text-green-600 font-semibold"> {boardgame.price}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
    );
};

export default HomePage;

import React from 'react';
import Boardgames from './boardgames'

const HomePage: React.FC = () => {
    return (
            <div>
                <h1>Hello, this is the Home Page!</h1>
                <div>
                    <Boardgames />
                </div>
            </div>
    );
};

export default HomePage;

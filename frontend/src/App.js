import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
	
    state = {
    	cells: []
    };

    componentDidMount() {
        this.startGame();
    }

    startGame = () => {
        fetch('/api/setup?x=5&y=5')
        .then(res => res.json())
        .then((data) => {
          this.setState({ cells: data })
        })
        .catch(console.log)
//            .then(response => response.text())
//            .then(message => {
//                this.setState({message: message});
//            });
    };
    
    render() {
        return (
    		<table className="board">
				{this.state.cells.map((contact, ind) => (
					<tr>
	                    	test
					</tr>
	                ))}
	        </table>
        );
    }
}

export default App;

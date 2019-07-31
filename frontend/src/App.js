import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
	
    constructor(props) {
		super(props);
		this.handleClick = this.handleClick.bind(this);
	}
	
    state = {
    	cells: [],
    	id: 0
    };

    componentDidMount() {
        this.startGame();
    }

    startGame = () => {
        fetch('/api/setup?x=7&y=7&mines=10')
        .then(res => res.json())
        .then((data) => {
          this.setState({ cells: data.cellsCurrent, id: data.id })
        })
        .catch(console.log)
//            .then(response => response.text())
//            .then(message => {
//                this.setState({message: message});
//            });
    };

	play = () => {
		var xPos = 3;	// TODO use real values
		var yPos = 4;
		fetch('/api/play', {
      		  method: 'POST',
      		  body: JSON.stringify({
      		    x: xPos,
      		    y: yPos,
      		  })
      		})
      		.then(res => res.json())
              .then((data) => {
                  this.setState({ cells: data })
                })
            .catch(console.log);
	};
  
	handleClick(e, xPos, yPos) {
    	var gameId = this.state.id;
    	if (e.nativeEvent.which === 1) {
    		fetch('/api/play', {
      		  method: 'POST',
      		  headers: {
      		      'Accept': 'application/json',
      		      'Content-Type': 'application/json'
      		  },
      		  body: JSON.stringify({
      			id: gameId,
      		    x: xPos,
      		    y: yPos,
      		  })
      		})
      		.then(res => res.json())
              .then((data) => {
                  this.setState({ cells: data })
                })
            .catch(console.log);
		} else if (e.nativeEvent.which === 3) {
			e.preventDefault();
			
			fetch('/api/flag', {
	      		  method: 'POST',
	      		  headers: {
	      		      'Accept': 'application/json',
	      		      'Content-Type': 'application/json'
	      		  },
	      		  body: JSON.stringify({
	      			id: gameId,
	      		    x: xPos,
	      		    y: yPos,
	      		  })
	      		})
	      	.then(res => res.json())
	              .then((data) => {
	                  this.setState({ cells: data })
	                })
	        .catch(console.log);
		}
    }
  
    render() {
        return (
        	<table className="board">
				{this.state.cells.map((contact, ind) => (
					<tr>
						{contact.map((subcontact, ind2) => (
						  <td className={'n' + subcontact} onClick={(e) => this.handleClick(e, ind, ind2)} onContextMenu={(e) => this.handleClick(e, ind, ind2)}>
							<div>{subcontact}</div>
	                      </td>
						))}
					</tr>
				))}
			</table>
        );
    }
}

export default App;

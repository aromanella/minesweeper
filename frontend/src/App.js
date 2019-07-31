import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

	constructor(props) {
		super(props);
		this.handleClick = this.handleClick.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}
	
    state = {
    	cells: [],
    	id: 0,
    	elapsed: '00:00:00',
    	gameOver: false
    };

    componentDidMount() {
        this.startGame();
    }

    startGame = () => {
        fetch('/api/setup?x=7&y=7&mines=10')
        .then(res => res.json())
        .then((data) => {
          this.setState({ cells: data.cellsCurrent, id: data.id, gameOver: false })
        })
        .catch(console.log)
//            .then(response => response.text())
//            .then(message => {
//                this.setState({message: message});
//            });
    };
    
    handleSubmit(e) {
        e.preventDefault();

        var x = this.refs.xSize.value;
        var y = this.refs.ySize.value;
        var mines = this.refs.mines.value;
        fetch('/api/setup?x=' + x + '&y=' + y + '&mines=' + mines)
        .then(res => res.json())
        .then((data) => {
          this.setState({ cells: data.cellsCurrent, id: data.id, gameOver: false })
        })
        .catch(console.log)
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
                  this.setState({ cells: data.cellsCurrent, elapsed: data.elapsedTime, gameOver: data.gameOver })
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
    	const isGameOver = this.state.gameOver;
    	let gameStatus = <div></div>;
    	if (isGameOver) {
    		gameStatus = <div><label>Game Over&nbsp;</label><label>{this.state.elapsed}</label></div>;
    	}
        return (
    		<React.Fragment>
	        	<div className="mainDiv">
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
	            </div>

	            <div class="gameStatus">
	            	{gameStatus}
	            </div>
	            
	            <div class="controlsDiv">
	            	<form onSubmit={this.handleSubmit}>
	            		<label>
	            			Rows:
	            			<input ref="xSize" type="text" name="x" defaultValue="7" size="2" />
	            		</label>
	            		<label>
	            			Columns:
	            			<input ref="ySize" type="text" name="y" defaultValue="7" size="2" />
	            		</label>
	            		<label>
            				Mines:
            				<input ref="mines" type="text" name="mines" defaultValue="10" size="2" />
            			</label>
	            		
	            		<input type="submit" value="New Game"></input>
	            	</form>
	            </div>
            </React.Fragment>
        );
    }
}

export default App;

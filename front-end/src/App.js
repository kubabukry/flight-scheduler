import './App.css';

function App() {

  fetch('http://localhost:8080/home', {
    headers: {
    },
    method: "get"

  });

  return (
    <div className="App">
     <h1>Hello world!</h1>
    </div>
  );
}

export default App;

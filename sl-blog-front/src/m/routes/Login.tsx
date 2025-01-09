import { useState } from 'react';
import {SubmitButton} from "../components/FormComp";

function Login() {

	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [inLogin, setInLogin] = useState(false);
	
	const login = async (username: string, password: string) => {
		setInLogin(true);
		await fetch('/login', {
			method: 'POST',
			body: JSON.stringify({
				username: username,
				password: password
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					window.location.replace('/m');
				} else {
					alert("Login failed.");
				}
			})
			.finally(() => {
				setInLogin(false);
			})
			.catch((err) => {
				console.log(err.message);
			});
			
	};

	const handleLogin = (e: React.FormEvent) => {
		e.preventDefault();
		login(username, password);
	};
	
	return (
		<>
			<form onSubmit={handleLogin}>
				<div className="flex flex-col">
					<div className="mb-4">
						<input type="text" id="username" placeholder="Username"
							autoComplete="username" className="input input-bordered"
							value={username}
							onChange={(e) => setUsername(e.target.value)} />
					</div>
					<div className="mb-4">
						<input type="password" id="password" autoComplete="password"
							className="input input-bordered" placeholder="Password"
							value={password}
							onChange={(e) => setPassword(e.target.value)} />
					</div>
					<div className="text-center">
						<SubmitButton text="Login" inProcess={inLogin} callback={handleLogin} classes="btn-sm btn-primary"/>
					</div>
				</div>
			</form>
		</>
	)
}

export default Login

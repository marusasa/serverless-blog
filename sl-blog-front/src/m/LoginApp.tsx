import { Outlet } from "react-router-dom";
 

function ManageApp() {

	return (
		<>
			<div className="container">
				<div className="p-4">
					<h1 className="text-5xl">Manage - Login</h1>
				</div>
				<div className="flex justify-center">
					<Outlet />
				</div>
			</div>
		</>
	)
}

export default ManageApp

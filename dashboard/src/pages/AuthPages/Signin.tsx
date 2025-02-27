// import { useDispatch } from "react-redux";
// import { useNavigate } from "react-router-dom";
// import { login } from "../../redux/authSlice";
// import { AppDispatch } from "../../redux/store";
import AuthLayout from "./AuthPageLayout";
import SignInForm from "../../components/auth/SignInForm";

const Signin: React.FC = () => {
  // const dispatch: AppDispatch = useDispatch();
  // const navigate = useNavigate();

  // const handleLogin = () => {
  //   const fakeToken = "abc123"; // Replace with a real token from API
  //   dispatch(login(fakeToken));
  //   navigate("/");
  // };

  return (
    <AuthLayout>
      <SignInForm />
    </AuthLayout>
  );
};

export default Signin;

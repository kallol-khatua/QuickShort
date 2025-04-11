import AuthLayout from "./AuthPageLayout";
import SignInForm from "../../components/auth/SignInForm";

const Signin: React.FC = () => {
  return (
    <AuthLayout>
      <SignInForm />
    </AuthLayout>
  );
};

export default Signin;

import React, { useState } from "react";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import { logout } from "../../redux/authSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { Navigate, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import axiosOrderInstance from "../../axiosOrderInstance";
import { SuccessApiResponse } from "../../helper/SuccessApiResponse";
import { setCurrentWorkspace, setIsLoaded } from "../../redux/workspaceSlice";

interface Plan {
  id: string;
  workspaceType: "PRO" | "BUSINESS";
  memberLimit: number;
  linkCreationLimitPerMonth: number;
  amount: number;
  amountPerMonth: number;
  planDuration: "MONTHLY" | "HALF_YEARLY" | "QUARTERLY" | "YEARLY";
  planDurationMonth: number;
  percentageOff: number;
}

type Data = {
  amount: number;
  id: string;
  orderStatus: string;
  paidAt: string | null;
  planEndDate: string | null;
  planId: string;
  planStartDate: string | null;
  razorpayOrderId: string;
  razorpayPaymentId: string | null;
  workspaceId: string;
};

type RazorpaySuccessResponse = {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
};

interface OrderData extends SuccessApiResponse {
  data: Data;
}

const plans: Plan[] = [
  {
    id: "209537d6-ed71-43b8-86a9-bafdb87a4dc6",
    workspaceType: "PRO",
    memberLimit: 10,
    linkCreationLimitPerMonth: 1000,
    amount: 4800.0,
    amountPerMonth: 800.0,
    planDuration: "HALF_YEARLY",
    planDurationMonth: 6,
    percentageOff: 20.0,
  },
  {
    id: "30f33c4b-38d4-4572-9185-991535721c47",
    workspaceType: "PRO",
    memberLimit: 10,
    linkCreationLimitPerMonth: 1000,
    amount: 8400.0,
    amountPerMonth: 700.0,
    planDuration: "YEARLY",
    planDurationMonth: 12,
    percentageOff: 30.0,
  },
  {
    id: "62f181b2-9485-412d-ba29-f26d909ea4f0",
    workspaceType: "PRO",
    memberLimit: 10,
    linkCreationLimitPerMonth: 1000,
    amount: 2700.0,
    amountPerMonth: 900.0,
    planDuration: "QUARTERLY",
    planDurationMonth: 3,
    percentageOff: 10.0,
  },
  {
    id: "76fe2e81-9d29-4218-a05c-7ba9c2bc56a4",
    workspaceType: "BUSINESS",
    memberLimit: 25,
    linkCreationLimitPerMonth: 5000,
    amount: 21000.0,
    amountPerMonth: 1750.0,
    planDuration: "YEARLY",
    planDurationMonth: 12,
    percentageOff: 30.0,
  },
  {
    id: "a9a74a74-647b-4c5e-a18d-e609681f3626",
    workspaceType: "BUSINESS",
    memberLimit: 25,
    linkCreationLimitPerMonth: 5000,
    amount: 2500.0,
    amountPerMonth: 2500.0,
    planDuration: "MONTHLY",
    planDurationMonth: 1,
    percentageOff: 0.0,
  },
  {
    id: "b0a85146-8a79-4840-9a25-90bfcf918a4f",
    workspaceType: "PRO",
    memberLimit: 10,
    linkCreationLimitPerMonth: 1000,
    amount: 1000.0,
    amountPerMonth: 1000.0,
    planDuration: "MONTHLY",
    planDurationMonth: 1,
    percentageOff: 0.0,
  },
  {
    id: "ce4ff831-cd4d-4e77-a656-927fca472048",
    workspaceType: "BUSINESS",
    memberLimit: 25,
    linkCreationLimitPerMonth: 5000,
    amount: 12000.0,
    amountPerMonth: 2000.0,
    planDuration: "HALF_YEARLY",
    planDurationMonth: 6,
    percentageOff: 20.0,
  },
  {
    id: "f3feb902-9813-41fc-bb62-58620d0e3ab5",
    workspaceType: "BUSINESS",
    memberLimit: 25,
    linkCreationLimitPerMonth: 5000,
    amount: 6750.0,
    amountPerMonth: 2250.0,
    planDuration: "QUARTERLY",
    planDurationMonth: 3,
    percentageOff: 10.0,
  },
];

// allow this upgrade page only for free account
const CheckType: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  return currentWorkspace?.workspaceId.type === "FREE" ? (
    children
  ) : (
    <Navigate to="/" />
  );
};

const abbreviation = {
  MONTHLY: "Monthly",
  HALF_YEARLY: "Half yearly",
  QUARTERLY: "Quarterly",
  YEARLY: "Yearly",
};

const Upgrade = () => {
  //   const [selectedPlan, setSelectedPlan] = useState({
  //     Business: "Basic",
  //     Enterprise: "Basic",
  //   });
  //   const dispatch = useDispatch();
  //   const [billingCycle, setBillingCycle] = useState("Yearly");
  //   const handlePlanChange = (type: "Business" | "Enterprise", plan: string) => {
  //     setSelectedPlan((prev) => ({ ...prev, [type]: plan }));
  //   };
  // const [selectedPlan, setSelectedPlan] = useState({});

  const [planType, setPlanType] = useState("YEARLY");

  const proPlans = plans.filter((plan) => plan.workspaceType === "PRO");
  const businessPlans = plans.filter(
    (plan) => plan.workspaceType === "BUSINESS"
  );

  const filteredProPlans = proPlans.filter(
    (plan) => plan.planDuration === planType
  );
  const filteredBusinessPlans = businessPlans.filter(
    (plan) => plan.planDuration === planType
  );
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Load all plans
  //   useEffect(() => {
  //     const loadLinks = async () => {
  //       try {
  //         // console.log("Loading links");
  //         // console.log(currentWorkspace.workspaceId.id);
  //         const response = await axiosPaymentInstance.get("");

  //         console.log(response.data);

  //         //   setTimeout(() => {
  //         //     setIsLinksLoaded(true);
  //         //     setReload(false);
  //         //   }, 250);
  //       } catch (err: unknown) {
  //         if (axios.isAxiosError(err) && err.response) {
  //           const errorData: ErrorApiResponse = err.response.data;

  //           // if unauthorized the logout using auth slice, protected route will take to signin page
  //           if (errorData.status_code === 401) {
  //             dispatch(logout());
  //           }
  //         } else {
  //           console.error("Unexpected error:", err);
  //         }
  //       }
  //     };
  //     loadLinks();
  //   }, [dispatch]);

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  // handle payment
  const handlePayment = async (plan: Plan) => {
    let order: Data | null = null;

    // create order first
    try {
      const response = await axiosOrderInstance.post<OrderData>("/", {
        workspaceId: currentWorkspace?.workspaceId.id,
        planId: plan.id,
      });

      order = response.data.data;
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401) {
          dispatch(logout());
        } else if (errorData.status_code === 405) {
          // send to billing page
          navigate(`/${currentWorkspace?.workspaceId.id}/settings/billing`);
        }
      } else {
        console.error("Unexpected error:", err);
      }

      return;
    }

    // if order is created then pay
    if (order) {
      const options = {
        key: import.meta.env.VITE_RAZORPAY_API_KEY,
        currency: "INR",
        name: "QuickShort",
        order_id: order.razorpayOrderId,
        description: "Subscription Payment",
        // handle payment verification
        handler: async function (response: RazorpaySuccessResponse) {
          //   console.log(response.razorpay_order_id);
          //   console.log(response.razorpay_payment_id);
          //   console.log(response.razorpay_signature);

          // send request to backend for payment verification
          await axiosOrderInstance.post<OrderData>(
            `/verify-payment?paymentId=${response.razorpay_payment_id}&orderId=${response.razorpay_order_id}&signature=${response.razorpay_signature}`
          );

          // reload all workspace
          dispatch(setIsLoaded(false));

          // set current to null so it will automatically match current workspace with recently creatd one
          dispatch(setCurrentWorkspace(null));

          // after verify send to billing page
          toast.success("Subscription added");
          navigate(`/${currentWorkspace?.workspaceId.id}/settings/billing`);
        },
        theme: {
          color: "#3399cc",
        },
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.open();

      //   rzp1.on("payment.failed", async function (response) {
      //     rzp1.close();
      //     //   console.log(response);
      //     // let failureResponse = {
      //     //   razorpay_payment_id: response.error.metadata.payment_id,
      //     //   razorpay_order_id: response.error.metadata.order_id,
      //     //   amount: amount,
      //     // };
      //     // console.log(paymentResponse);
      //     // const verifyResponse = await axiosInstance.post(
      //     //   `/api/advertiser/campaign/${campaignId}/add-money/verifyfailure`,
      //     //   failureResponse
      //     // );
      //     //   console.log(verifyResponse);
      //     // if (verifyResponse.status === 200) {
      //     //   toast.error(verifyResponse.data.message);
      //     //   navigate(`/advertiser/campaings/${campaignId}`);
      //     // }
      //   });
    }
  };

  return (
    <CheckType>
      <div className="min-h-full flex flex-col">
        <PageBreadcrumb pageTitle="Upgrade" />

        {/* Header */}
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 text-center mb-4">
          Flexible Plans Tailored to Fit Your Unique Needs!
        </h2>

        {/* Toggle Switch */}
        <div className="flex justify-center">
          <div className="flex items-center bg-gray-200 dark:bg-gray-700 p-1 rounded-full mb-6 ">
            {/* monthly */}
            <button
              onClick={() => setPlanType("MONTHLY")}
              className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                planType === "MONTHLY"
                  ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                  : "text-gray-600 dark:text-gray-300"
              }`}
            >
              Monthly
            </button>

            {/* quarterly */}
            <button
              onClick={() => setPlanType("QUARTERLY")}
              className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                planType === "QUARTERLY"
                  ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                  : "text-gray-600 dark:text-gray-300"
              }`}
            >
              Quarterly
            </button>

            {/* half yearly */}
            <button
              onClick={() => setPlanType("HALF_YEARLY")}
              className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                planType === "HALF_YEARLY"
                  ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                  : "text-gray-600 dark:text-gray-300"
              }`}
            >
              Half yearly
            </button>

            {/* yearly */}
            <button
              onClick={() => setPlanType("YEARLY")}
              className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                planType === "YEARLY"
                  ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                  : "text-gray-600 dark:text-gray-300"
              }`}
            >
              Yearly
            </button>
          </div>
        </div>

        {/* plan card */}
        <div className="flex flex-col md:flex-row gap-6 justify-center items-center">
          {filteredProPlans.map((plan) => {
            return (
              <div
                key={plan.id}
                className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-96"
              >
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                  Pro
                  {/* <span>most popular</span> */}
                </h2>
                <p className="text-3xl font-bold text-gray-900 dark:text-gray-100 mt-2">
                  ₹ {plan.amountPerMonth}{" "}
                  <span className="text-sm font-normal">
                    per month, billed{" "}
                    {abbreviation[`${plan.planDuration}`].toLowerCase()}
                  </span>
                </p>

                <ul className="mt-4 space-y-2 text-gray-700 dark:text-gray-300">
                  <li>✅ {plan.linkCreationLimitPerMonth} new links/mo</li>
                  <li>✅ {plan.memberLimit} users</li>
                  {plan.planDuration !== "MONTHLY" && (
                    <li>✅ {plan.percentageOff}% off</li>
                  )}
                  <li>✅ ₹ {plan.amount} total </li>
                </ul>

                <button
                  className="mt-4 w-full bg-black text-white dark:bg-white dark:text-black px-4 py-2 rounded-md font-medium"
                  onClick={() => handlePayment(plan)}
                >
                  Get started with {abbreviation[`${plan.planDuration}`]}
                </button>
              </div>
            );
          })}

          {filteredBusinessPlans.map((plan) => {
            return (
              <div
                key={plan.id}
                className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-96"
              >
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                  Business
                  {/* <span>most popular</span> */}
                </h2>
                <p className="text-3xl font-bold text-gray-900 dark:text-gray-100 mt-2">
                  ₹ {plan.amountPerMonth}{" "}
                  <span className="text-sm font-normal">
                    per month, billed{" "}
                    {abbreviation[`${plan.planDuration}`].toLowerCase()}
                  </span>
                </p>

                <ul className="mt-4 space-y-2 text-gray-700 dark:text-gray-300">
                  <li>✅ {plan.linkCreationLimitPerMonth} new links/mo</li>
                  <li>✅ {plan.memberLimit} users</li>
                  {plan.planDuration !== "MONTHLY" && (
                    <li>✅ {plan.percentageOff}% off</li>
                  )}
                  <li>✅ ₹ {plan.amount} total </li>
                </ul>

                <button
                  className="mt-4 w-full bg-black text-white dark:bg-white dark:text-black px-4 py-2 rounded-md font-medium"
                  onClick={() => handlePayment(plan)}
                >
                  Get started with {abbreviation[`${plan.planDuration}`]}
                </button>
              </div>
            );
          })}
        </div>
      </div>
    </CheckType>
  );
};

export default Upgrade;

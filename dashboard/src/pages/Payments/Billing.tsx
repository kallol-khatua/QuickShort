import { useDispatch, useSelector } from "react-redux";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import { RootState } from "../../redux/store";
import { useEffect, useState } from "react";
import { plans, Plan } from "../../helper/Plan";
import axiosOrderInstance from "../../axiosOrderInstance";
import { SuccessApiResponse } from "../../helper/SuccessApiResponse";
import toast from "react-hot-toast";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import axios from "axios";
import { logout } from "../../redux/authSlice";

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

const ListInvitations: React.FC = () => {
  return (
    <div className="dark:bg-gray-900 rounded-lg">
      <div
        // key={invitation.id}
        className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
      >
        <div className="sm:flex sm:justify-between sm:items-center">
          {/* title */}
          <div className="flex items-center space-x-2">
            <div>
              {/* <Avatar src={invitation.userId.profileImageURL} size="medium" /> */}
            </div>

            <div className="flex flex-col">
              <div className="font-semibold dark:text-white">sokdhsj</div>
              <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                psodjf
              </p>
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
              // onClick={() => handleVerifyInvitation(invitation)}
            >
              Allow
            </button>
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
              // onClick={() => handleRejectInvitation(invitation)}
            >
              Reject
            </button>
          </div>
        </div>

        {/* triple dot icon */}
      </div>
      <div
        // key={invitation.id}
        className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
      >
        <div className="sm:flex sm:justify-between sm:items-center">
          {/* title */}
          <div className="flex items-center space-x-2">
            <div>
              {/* <Avatar src={invitation.userId.profileImageURL} size="medium" /> */}
            </div>

            <div className="flex flex-col">
              <div className="font-semibold dark:text-white">sokdhsj</div>
              <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                psodjf
              </p>
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
              // onClick={() => handleVerifyInvitation(invitation)}
            >
              Allow
            </button>
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
              // onClick={() => handleRejectInvitation(invitation)}
            >
              Reject
            </button>
          </div>
        </div>

        {/* triple dot icon */}
      </div>
    </div>
  );
};

const Billing = () => {
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );
  const dispatch = useDispatch();
  const [currPlans, setCurrPlans] = useState<Plan[]>([]);
  const [nextPlan, setNextPlan] = useState<Plan>();

  const getDate = (data: string): string => {
    const date = new Date(data);

    const options: Intl.DateTimeFormatOptions = {
      month: "short",
      day: "2-digit",
      year: "numeric",
    };
    const formattedDate = date.toLocaleDateString("en-US", options);

    return formattedDate;
  };

  // Choose default plan for yearly
  useEffect(() => {
    const plan = currPlans.filter((plan) => plan.planDuration === "YEARLY")[0];
    setNextPlan(plan);
  }, [currPlans]);

  // match plans with current workspace type
  useEffect(() => {
    const plansforWorkspace = plans.filter(
      (plan) => plan.workspaceType === currentWorkspace?.workspaceId.type
    );

    setCurrPlans(plansforWorkspace);
  }, [currentWorkspace?.workspaceId.type]);

  // Create razorpay order from the backend and open rozorpay payment page
  const handlePayNow = async () => {
    let order: Data | null = null;

    const workspaceId = currentWorkspace?.workspaceId.id;
    const planId = nextPlan?.id;

    if (!workspaceId || !planId) {
      toast.error("Provide all data");
      return;
    }

    // create order first
    try {
      const response = await axiosOrderInstance.post<OrderData>("/repay", {
        workspaceId: currentWorkspace.workspaceId.id,
        planId: nextPlan.id,
      });

      order = response.data.data;
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401) {
          dispatch(logout());
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
          // await axiosOrderInstance.post<OrderData>(
          //   `/verify-payment?paymentId=${response.razorpay_payment_id}&orderId=${response.razorpay_order_id}&signature=${response.razorpay_signature}`
          // );

          console.log(response);

          // reload all workspace
          // dispatch(setIsLoaded(false));

          // set current to null so it will automatically match current workspace with recently creatd one
          // dispatch(setCurrentWorkspace(null));

          // after verify send to billing page
          toast.success("Subscription added");
          // navigate(`/${currentWorkspace?.workspaceId.id}/settings/billing`);
        },
        theme: {
          color: "#3399cc",
        },
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.open();
    }
  };

  return (
    <div className="min-h-full flex flex-col">
      <PageBreadcrumb pageTitle="Billing" />

      <div className="flex flex-col flex-1 min-h-0">
        <div className="w-full flex justify-between mb-4">
          <div className="flex items-center space-x-2 text-gray-700 dark:text-gray-300">
            <span>
              Next billng date{" "}
              {getDate(currentWorkspace?.workspaceId.nextBillingDate || "")}
            </span>
          </div>

          <button
            className="flex items-center justify-center p-3 font-medium text-white rounded-lg bg-gray-900 text-theme-sm hover:bg-gray-800 dark:bg-white dark:text-black dark:bg-gray-300"
            onClick={handlePayNow}
          >
            Pay now
          </button>
        </div>

        <ListInvitations />
      </div>
    </div>
  );
};

export default Billing;

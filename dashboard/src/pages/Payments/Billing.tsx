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
import RotatingLoader from "../../components/ui/loader/RotatingLoader";
import { Navigate } from "react-router-dom";
import { setCurrentWorkspace, setIsLoaded } from "../../redux/workspaceSlice";
import ChoosePlanModal from "./ChoosePlanModal";

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

interface AllOrders extends SuccessApiResponse {
  data: Data[];
}

// List all orders
const ListOrders: React.FC<{
  allOrders: Data[];
  handleReload: () => void;
  handlePayNow: () => void;
}> = ({ allOrders, handleReload, handlePayNow }) => {
  const dispatch = useDispatch();

  // calcel order when satus = awaiting payment
  const handleCancelOrder = async (order: Data) => {
    try {
      await axiosOrderInstance.post(`/${order.id}/cancel`);

      toast.success("Order cancelled successfully!");
      handleReload();
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
  };

  return (
    <div className="dark:bg-gray-900 rounded-lg">
      {allOrders.map((order) => {
        return (
          <div
            key={order.id}
            className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
          >
            <div className="sm:flex sm:justify-between sm:items-center">
              <div className="flex items-center space-x-2">
                <div className="flex flex-col">
                  <div className="font-semibold dark:text-white">
                    Status: {order.orderStatus}
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                    Amount: {order.amount}
                  </p>
                  <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                    Order Id: {order.id}
                  </p>
                </div>
              </div>

              {/* Action buttons */}
              {order.orderStatus === "AWAITING_PAYMENT" && (
                <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
                  <button
                    className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
                    onClick={handlePayNow}
                    // onClick={() => handleVerifyInvitation(invitation)}
                  >
                    Pay now
                  </button>
                  <button
                    className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
                    onClick={() => handleCancelOrder(order)}
                  >
                    Cancel order
                  </button>
                </div>
              )}
            </div>

            {/* Action buttons */}
            {order.orderStatus === "COMPLETED" && (
              <div>
                <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                  Plan start date: {order.planStartDate}
                </p>
                <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                  Plan end date: {order.planEndDate}
                </p>
                <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                  Paid at: {order.paidAt}
                </p>
              </div>
            )}

            {/* triple dot icon */}
          </div>
        );
      })}
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
  const [nextPlanId, setnextPlanId] = useState<string>("");
  const [allOrders, setAllorders] = useState<Data[]>([]);
  const [isDataLoaded, setIsDataLoaded] = useState(false);
  const [reload, setReload] = useState<boolean>(false);
  const [isChoosePlanModalOpen, setIsChoosePlanModalOpen] =
    useState<boolean>(false);

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

  const handleReload = () => {
    setReload((prev) => !prev);
  };

  // load previous order
  useEffect(() => {
    const loadOrders = async () => {
      try {
        const response = await axiosOrderInstance.get<AllOrders>(
          `/${currentWorkspace?.workspaceId.id}/all-orders`
        );

        setAllorders(response.data.data);
        setReload(false);
        setTimeout(() => {
          setIsDataLoaded(true);
        }, 250);
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
    };

    loadOrders();
  }, [currentWorkspace?.workspaceId.id, dispatch, reload]);

  // Choose default plan for yearly
  useEffect(() => {
    const plan = currPlans.filter((plan) => plan.planDuration === "YEARLY")[0];

    if (plan) {
      setNextPlan(plan);
      setnextPlanId(plan.id);
    }
  }, [currPlans]);

  useEffect(() => {
    const plan = currPlans.filter((plan) => plan.id === nextPlanId)[0];
    if (plan) {
      setNextPlan(plan);
    }
  }, [currPlans, nextPlanId]);

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
      if (currentWorkspace?.workspaceId.type === "FREE") {
        toast.error("Please upgrade to premium plan");
        return;
      }
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
          // send request to backend for payment verification
          try {
            await axiosOrderInstance.post<OrderData>(
              `/verify-payment/repay?paymentId=${response.razorpay_payment_id}&orderId=${response.razorpay_order_id}&signature=${response.razorpay_signature}`
            );

            // after verify reload orders
            toast.success("Payment successfull");

            setTimeout(() => {
              // reload all workspace
              dispatch(setIsLoaded(false));

              // set current to null so it will automatically match current workspace with recently updated data
              dispatch(setCurrentWorkspace(null));

              handleReload();
            }, 250);
          } catch (error) {
            console.log("Error while verifying payment", error);
          }
        },
        theme: {
          color: "#3399cc",
        },
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.open();
    }
  };

  const handleChooseAnotherPlan = () => {
    setIsChoosePlanModalOpen((prev) => !prev);
  };

  const handlesetNextPlanId = (id: string) => {
    setnextPlanId(id);
  };

  return (
    <div className="min-h-full flex flex-col">
      <PageBreadcrumb pageTitle="Billing" />

      <div className="flex flex-col flex-1 min-h-0">
        {currentWorkspace?.workspaceId.type !== "FREE" ? (
          <div className="w-full flex justify-between mb-4">
            <div className="flex items-center space-x-2 text-gray-700 dark:text-gray-300">
              <span>
                Next billng date{" "}
                {getDate(currentWorkspace?.workspaceId.nextBillingDate || "")}
              </span>
            </div>

            <div className="flex gap-3">
              {isChoosePlanModalOpen && nextPlan && (
                <ChoosePlanModal
                  handleChooseAnotherPlan={handleChooseAnotherPlan}
                  nextPlan={nextPlan}
                  handlesetNextPlanId={handlesetNextPlanId}
                  currPlans={currPlans}
                />
              )}

              <button
                className="flex items-center justify-center p-3 font-medium text-white rounded-lg bg-gray-900 text-theme-sm hover:bg-gray-800 dark:bg-white dark:text-black dark:bg-gray-300"
                onClick={handleChooseAnotherPlan}
              >
                Choose next plan
              </button>

              <button
                className="flex items-center justify-center p-3 font-medium text-white rounded-lg bg-gray-900 text-theme-sm hover:bg-gray-800 dark:bg-white dark:text-black dark:bg-gray-300"
                onClick={handlePayNow}
              >
                Pay now
              </button>
            </div>
          </div>
        ) : (
          <Navigate to={`/${currentWorkspace.workspaceId.id}/upgrade`} />
        )}

        {isDataLoaded ? (
          <ListOrders
            allOrders={allOrders}
            handleReload={handleReload}
            handlePayNow={handlePayNow}
          />
        ) : (
          <div className="flex-1 min-h-0 flex justify-center items-center w-full">
            <div className="w-full">
              <RotatingLoader />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Billing;

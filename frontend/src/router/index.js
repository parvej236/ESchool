import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import HomeView from "@/views/HomeView.vue";
import DashboardView from "@/views/DashboardView.vue";
import LoginView from "@/views/login/LoginView.vue";
import RegisterView from "@/views/login/RegisterView.vue";
import ForgotPasswordView from "@/views/login/ForgotPasswordView.vue";
import ResetPasswordView from "@/views/login/ResetPasswordView.vue";
import ChangePasswordView from "@/views/login/ChangePasswordView.vue";
import ProfileView from "@/views/login/ProfileView.vue";
import CreateHomeSlideView from "@/views/homeslides/CreateHomeSlideView.vue";
import EditHomeSlideView from "@/views/homeslides/EditHomeSlideView.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
    meta: { requiresAuth: false },
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: { requiresAuth: false, redirectIfAuthenticated: true },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: { requiresAuth: false, redirectIfAuthenticated: true },
  },
  {
    path: "/forgot-password",
    name: "forgot-password",
    component: ForgotPasswordView,
    meta: { requiresAuth: false },
  },
  {
    path: "/reset-password",
    name: "reset-password",
    component: ResetPasswordView,
    meta: { requiresAuth: false },
  },
  {
    path: "/change-password",
    name: "change-password",
    component: ChangePasswordView,
    meta: { requiresAuth: true },
  },
  {
    path: "/view-profile",
    name: "view-profile",
    component: ProfileView,
    meta: { requiresAuth: true },
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    meta: { requiresAuth: true },
  },
  {
    path: "/admin/create-home-slide",
    name: "create-home-slide",
    component: CreateHomeSlideView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/edit-home-slide/:id",
    name: "edit-home-slide",
    component: EditHomeSlideView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      // restore scroll position when using browser back/forward
      return { ...savedPosition, behavior: "smooth" };
    } else {
      // smooth scroll to top for new navigation
      return { top: 0, behavior: "smooth" };
    }
  },
});

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Redirect to login if not authenticated
    next({ name: "login", query: { redirect: to.fullPath } });
    return;
  }

  // Check if route requires admin role
  if (to.meta.requiresAdmin) {
    // Fetch user info to get role if not already loaded
    if (!authStore.user) {
      await authStore.fetchUserInfo();
    }

    if (!authStore.isAdmin) {
      // Redirect to dashboard if not admin
      next({ name: "dashboard" });
      return;
    }
  }

  if (to.meta.redirectIfAuthenticated && authStore.isAuthenticated) {
    // Redirect to dashboard if already authenticated and trying to access login/register
    next({ name: "dashboard" });
  } else {
    // Allow navigation
    next();
  }
});

export default router;

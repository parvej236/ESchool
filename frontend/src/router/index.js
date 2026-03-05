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
import UsersView from "@/views/UsersView.vue";

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
    path: "/users",
    name: "users",
    component: UsersView,
    meta: { requiresAuth: true },
  },
{
  path: '/dashboard/home-slides',
  name: 'home-slides',
  component: () => import('@/views/HomeSlidesView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/dashboard/home-slides/create',
  name: 'home-slides-create',
  component: () => import('@/views/HomeSlideFormView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/dashboard/home-slides/edit/:id',
  name: 'home-slides-edit',
  component: () => import('@/views/HomeSlideFormView.vue'),
  meta: { requiresAuth: true }
}
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

  // Wait for initial auth check so we don't allow dashboard access with stale token
  if (!authStore.authReady) {
    await authStore.validateAuth();
  }

  // Check if route requires authentication (use validated state so stale token can't access)
  if (to.meta.requiresAuth && !authStore.isAuthenticatedAfterCheck) {
    next({ name: "login", query: { redirect: to.fullPath } });
    return;
  }

  if (to.meta.redirectIfAuthenticated && authStore.isAuthenticatedAfterCheck) {
    next({ name: "dashboard" });
  } else {
    next();
  }
});

export default router;

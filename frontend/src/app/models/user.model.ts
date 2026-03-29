export interface UserResponse {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName: string;
  enabled: boolean;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  lastLogin?: string;
  failedLoginAttempts: number;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface UserRequest {
  username: string;
  email: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  enabled?: boolean;
  roles?: string[];
}

// Type alias for backward compatibility
export type User = UserResponse;

// Made with Bob

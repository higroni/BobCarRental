/**
 * Vehicle Type models matching backend VehicleType entity and DTOs
 */

export interface VehicleType {
  id: number;
  typeId: string;
  typeName: string;
  description?: string;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface VehicleTypeRequest {
  typeId: string;
  typeName: string;
  description?: string;
  isActive?: boolean;
}

export interface VehicleTypeResponse {
  id: number;
  typeId: string;
  typeName: string;
  description?: string;
  isActive?: boolean;
  createdAt: string;
  updatedAt: string;
}

// Made with Bob

export interface Metadata {
  name: string;
  generateName?: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}

export interface MovieTaskGroupSpec {
  displayName: string;
  priority?: number;
}

export interface PostGroupStatus {
  photoCount: number;
}

export interface MovieTaskSpec {
  displayName: string;
  description?: string;
  url: string;
  cover?: string;
  priority?: number;
  groupName: string;
}

export interface MovieTask {
  spec: MovieTaskSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface MovieTaskGroup {
  spec: MovieTaskGroupSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  status: PostGroupStatus;
}

export interface MovieTaskList {
  page: number;
  size: number;
  total: number;
  totalPages: number;
  items: Array<MovieTask>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface MovieTaskGroupList {
  page: number;
  size: number;
  total: number;
  items: Array<MovieTaskGroup>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

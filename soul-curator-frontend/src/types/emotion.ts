// 情感探索相关类型定义

export interface QuestionOption {
  id: string;
  title: string;
  description: string;
  references: string[];
}

export interface Question {
  id: number;
  title: string;
  subtitle?: string;
  options: QuestionOption[];
}

export interface ProgressStep {
  id: number;
  label: string;
  icon: string;
  description?: string;
}

export interface UserSelection {
  questionId: number;
  optionId: string;
  selectedAt: Date;
}

// 进度步骤数据
export const PROGRESS_STEPS: ProgressStep[] = [
  { id: 1, label: '灵魂之门', icon: 'door-open' },
  { id: 2, label: '情感共鸣', icon: 'heart' },
  { id: 3, label: '主题焦点', icon: 'target' },
  { id: 4, label: '叙事艺术', icon: 'book-open' },
  { id: 5, label: '视觉美学', icon: 'palette' },
  { id: 6, label: '角色灵魂', icon: 'user' },
  { id: 7, label: '生成画像', icon: 'sparkles' },
];